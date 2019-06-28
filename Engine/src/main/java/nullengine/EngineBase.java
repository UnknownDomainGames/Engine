package nullengine;

import nullengine.event.EventBus;
import nullengine.event.SimpleEventBus;
import nullengine.event.asm.AsmEventListenerFactory;
import nullengine.event.engine.EngineEvent;
import nullengine.mod.ModContainer;
import nullengine.mod.ModManager;
import nullengine.mod.dummy.DummyModContainer;
import nullengine.mod.exception.InvalidModMetadataException;
import nullengine.mod.impl.AbstractModAssets;
import nullengine.mod.impl.EngineModManager;
import nullengine.mod.init.ModInitializer;
import nullengine.mod.java.JavaModAssets;
import nullengine.mod.misc.SimpleModMetadata;
import nullengine.util.ClassPathUtils;
import nullengine.util.RuntimeEnvironment;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static nullengine.util.ClassPathUtils.getDirectoriesInClassPath;
import static nullengine.util.ClassPathUtils.getFilesInClassPath;
import static org.apache.commons.lang3.SystemUtils.*;

public abstract class EngineBase implements Engine {

    protected final Logger logger = LoggerFactory.getLogger("Engine");

    protected final List<Runnable> shutdownListeners = new LinkedList<>();

    private RuntimeEnvironment runtimeEnvironment;

    protected EventBus eventBus;
    protected EngineModManager modManager;

    private boolean initialized = false;
    private boolean running = false;
    private boolean markedTermination = false;

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public RuntimeEnvironment getRuntimeEnvironment() {
        return runtimeEnvironment;
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public ModManager getModManager() {
        return modManager;
    }

    @Override
    public void initEngine() {
        if (initialized) {
            throw new IllegalStateException("Engine has been initialized.");
        }
        initialized = true;

        logger.info("Initializing engine!");

        initExceptionHandler();
        initEnvironment();
        printSystemInfo();

        eventBus = SimpleEventBus.builder().eventListenerFactory(AsmEventListenerFactory.create()).build();

        loadMods();
    }

    private void initExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            logger.error("Caught unhandled exception!!! Engine will terminate!", e);
            // TODO: Crash report
            System.exit(1);
        });
    }

    private void initEnvironment() {
        try {
            if (Files.isDirectory(Path.of(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI()))) {
                runtimeEnvironment = RuntimeEnvironment.ENGINE_DEVELOPMENT;
                return;
            }
        } catch (URISyntaxException ignored) {
        }

        if (!ClassPathUtils.getDirectoriesInClassPath().isEmpty()) {
            runtimeEnvironment = RuntimeEnvironment.MOD_DEVELOPMENT;
            return;
        }

        runtimeEnvironment = RuntimeEnvironment.DEPLOYMENT;
    }

    private void printSystemInfo() {
        Logger logger = Platform.getLogger();
        logger.info("----- System Information -----");
        logger.info("\tOperating System: {} ({}) version {}", OS_NAME, OS_ARCH, OS_VERSION);
        logger.info("\tJava Version: {} ({}), {}", JAVA_VERSION, JAVA_VM_VERSION, JAVA_VENDOR);
        logger.info("\tJVM Information: {} ({}), {}", JAVA_VM_NAME, JAVA_VM_INFO, JAVA_VM_VENDOR);
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long maxMemory = runtime.maxMemory();
        logger.info("\tMax Memory: {} bytes ({} MB)", maxMemory, maxMemory / 1024L / 1024L);
        logger.info("\tTotal Memory: {} bytes ({} MB)", totalMemory, totalMemory / 1024L / 1024L);
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        List<String> jvmFlags = runtimeMXBean.getInputArguments();
        StringBuilder formattedFlags = new StringBuilder();
        for (String flag : jvmFlags) {
            if (formattedFlags.length() > 0) {
                formattedFlags.append(' ');
            }
            formattedFlags.append(flag);
        }
        logger.info("\tJVM Flags ({} totals): {}", jvmFlags.size(), formattedFlags.toString());
        logger.info("\tEngine Version: {}", Platform.getVersion());
        logger.info("\tEngine Side: {}", getSide().name());
        logger.info("\tRuntime Environment: {}", runtimeEnvironment.name());
        logger.info("------------------------------");
    }

    private void loadMods() {
        logger.info("Loading Mods.");
        modManager = new EngineModManager();
        try {
            loadEngineDummyMod();
            loadDirMod();
            loadDevEnvMod();

            Collection<ModContainer> loadedMods = modManager.getLoadedMods();
            logger.info("Loaded mods: [" + StringUtils.join(loadedMods.stream().map(modContainer -> modContainer.getId() + "(" + modContainer.getVersion() + ")").iterator(), ", ") + "]");

            ModInitializer initializer = new ModInitializer(this);
            for (ModContainer mod : loadedMods) {
                if (mod instanceof DummyModContainer) {
                    continue;
                }
                initializer.init(mod);
            }

        } catch (IOException | URISyntaxException e) {
            // TODO: Crash report
            logger.error("Cannot load mods.", e);
        }
    }

    private void loadEngineDummyMod() throws IOException, URISyntaxException {
        var engineMod = new DummyModContainer(SimpleModMetadata.builder().id("engine").version(Platform.getVersion()).build());
        engineMod.setClassLoader(getClass().getClassLoader());
        Path engineJarPath = Path.of(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
        AbstractModAssets modAssets;
        if (Platform.getEngine().getRuntimeEnvironment() == RuntimeEnvironment.ENGINE_DEVELOPMENT) {
            modAssets = new JavaModAssets(getDirectoriesInClassPath(), getClass().getClassLoader());
        } else {
            modAssets = new JavaModAssets(List.of(engineJarPath), getClass().getClassLoader());
        }
        modAssets.setMod(engineMod);
        engineMod.setAssets(modAssets);
        modManager.addDummyModContainer(engineMod);
    }

    private void loadDirMod() throws IOException {
        Path modFolder = Path.of("mods");
        if (!Files.exists(modFolder)) {
            try {
                Files.createDirectories(modFolder);
            } catch (IOException e) {
                logger.warn(e.getMessage(), e);
            }
        }

        try (var stream = Files.find(modFolder, 1,
                (path, basicFileAttributes) -> path.getFileName().toString().endsWith(".jar"))) {
            stream.forEach(modManager::loadMod);
        }
    }

    private void loadDevEnvMod() {
        if (runtimeEnvironment != RuntimeEnvironment.MOD_DEVELOPMENT)
            return;

        loadClassPathMods();

        modManager.loadDevEnvMod();
    }

    private void loadClassPathMods() {
        for (Path path : getFilesInClassPath()) {
            try {
                modManager.loadMod(path);
            } catch (InvalidModMetadataException ignored) {
            }
        }
    }

    @Override
    public void runEngine() {
        if (running) {
            throw new IllegalStateException("Engine is running.");
        }
        running = true;
    }

    @Override
    public synchronized void terminate() {
        if (markedTermination) {
            return;
        }

        markedTermination = true;
        eventBus.post(new EngineEvent.MarkedTermination(this));
        logger.info("Marked engine terminated!");
    }

    @Override
    public boolean isMarkedTermination() {
        return markedTermination;
    }

    @Override
    public void addShutdownListener(Runnable runnable) {
        shutdownListeners.add(runnable);
    }
}
