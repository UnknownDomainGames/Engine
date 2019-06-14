package unknowndomain.engine;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unknowndomain.engine.event.EventBus;
import unknowndomain.engine.event.SimpleEventBus;
import unknowndomain.engine.event.asm.AsmEventListenerFactory;
import unknowndomain.engine.event.engine.EngineEvent;
import unknowndomain.engine.mod.ModContainer;
import unknowndomain.engine.mod.ModManager;
import unknowndomain.engine.mod.dummy.DummyModContainer;
import unknowndomain.engine.mod.exception.InvalidModDescriptorException;
import unknowndomain.engine.mod.impl.EngineModManager;
import unknowndomain.engine.mod.java.JavaModAssets;
import unknowndomain.engine.mod.java.dev.DevModAssets;
import unknowndomain.engine.mod.misc.DefaultModDescriptor;
import unknowndomain.engine.util.ClassPathUtils;
import unknowndomain.engine.util.RuntimeEnvironment;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.apache.commons.lang3.SystemUtils.*;
import static unknowndomain.engine.util.ClassPathUtils.getDirectoriesInClassPath;
import static unknowndomain.engine.util.ClassPathUtils.getFilesInClassPath;

public abstract class EngineBase implements Engine {

    protected final Logger logger = LoggerFactory.getLogger("Engine");

    protected final List<Runnable> shutdownListeners = new LinkedList<>();

    private RuntimeEnvironment runtimeEnvironment;

    protected EventBus eventBus;
    protected EngineModManager modManager;

    private boolean initialized = false;
    private boolean running = false;
    private boolean terminated = false;

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

        initEnvironment();
        printSystemInfo();

        eventBus = SimpleEventBus.builder().eventListenerFactory(AsmEventListenerFactory.create()).build();

        loadMods();
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

            // TODO: Move it
            for (ModContainer mod : loadedMods) {
                if (mod instanceof DummyModContainer)
                    continue;

                eventBus.register(mod.getInstance());
            }

            logger.info("Loaded mods: [" + StringUtils.join(loadedMods.stream().map(modContainer -> modContainer.getModId() + "@" + modContainer.getVersion()).iterator(), ",") + "]");
        } catch (IOException | URISyntaxException e) {
            // TODO: Crash report
            logger.error("Cannot load mods.", e);
        }
    }

    private void loadEngineDummyMod() throws IOException, URISyntaxException {
        var engineMod = new DummyModContainer(DefaultModDescriptor.builder().modId("engine").version(Platform.getVersion()).build());
        engineMod.setClassLoader(getClass().getClassLoader());
        Path engineJarPath = Path.of(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
        if (Platform.getEngine().getRuntimeEnvironment() == RuntimeEnvironment.ENGINE_DEVELOPMENT) {
            engineMod.setAssets(new DevModAssets(getDirectoriesInClassPath()));
        } else {
            FileSystem fileSystem = FileSystems.newFileSystem(engineJarPath, getClass().getClassLoader());
            engineMod.setAssets(new JavaModAssets(fileSystem));
        }
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

        Files.find(modFolder, 1,
                (path, basicFileAttributes) -> path.getFileName().toString().endsWith(".jar"))
                .forEach(modManager::loadMod);
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
            } catch (InvalidModDescriptorException ignored) {
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
        if (terminated) {
            return;
        }

        terminated = true;
        eventBus.post(new EngineEvent.MarkedTermination(this));
        logger.info("Marked engine terminated!");
    }

    @Override
    public boolean isTerminated() {
        return terminated;
    }

    @Override
    public void addShutdownListener(Runnable runnable) {
        shutdownListeners.add(runnable);
    }
}
