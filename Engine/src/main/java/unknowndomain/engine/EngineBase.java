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
import unknowndomain.engine.mod.impl.EngineModManager;
import unknowndomain.engine.util.RuntimeEnvironment;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.apache.commons.lang3.SystemUtils.*;

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

        if (Arrays.stream(JAVA_CLASS_PATH.split(";")).anyMatch(path -> Files.isDirectory(Paths.get(path)))) {
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

        Path modFolder = Paths.get("mods");
        if (!Files.exists(modFolder)) {
            try {
                Files.createDirectory(modFolder);
            } catch (IOException e) {
                logger.warn(e.getMessage(), e);
            }
        }

        try {
            modManager.loadMod(createFolderModCollector(modFolder));
            modManager.loadDevEnvMod();

            Collection<ModContainer> loadedMods = modManager.getLoadedMods();

            logger.info("Loaded mods: [" + StringUtils.join(loadedMods.stream().map(modContainer -> modContainer.getModId() + "@" + modContainer.getDescriptor().getVersion()).iterator(), ",") + "]");

            loadedMods.forEach(modContainer -> eventBus.register(modContainer.getInstance()));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

//        Platform.getLogger().info("Initializing Mods!");
//        getContext().post(new EngineEvent.ModInitializationEvent(this));
//        Platform.getLogger().info("Finishing Construction!");
//        getContext().post(new EngineEvent.ModConstructionFinish(this));
    }

    private Iterator<Path> createFolderModCollector(Path folder) throws IOException {
        if (!Files.exists(folder)) {
            throw new IllegalStateException("Path is not exists.");
        }

        if (!Files.isDirectory(folder)) {
            throw new IllegalArgumentException("Path must be directory.");
        }

        return Files.find(folder, 1, (path, basicFileAttributes) -> path.getFileName().toString().endsWith(".jar")).iterator();
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
