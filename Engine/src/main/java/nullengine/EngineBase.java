package nullengine;

import nullengine.event.EventBus;
import nullengine.event.SimpleEventBus;
import nullengine.event.asm.AsmEventListenerFactory;
import nullengine.event.engine.EngineEvent;
import nullengine.mod.ModContainer;
import nullengine.mod.ModManager;
import nullengine.mod.impl.EngineModManager;
import nullengine.registry.Registries;
import nullengine.registry.RegistryManager;
import nullengine.registry.impl.SimpleRegistryManager;
import nullengine.util.ClassPathUtils;
import nullengine.util.CrashHandlerImpl;
import nullengine.util.RuntimeEnvironment;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static org.apache.commons.lang3.SystemUtils.*;

public abstract class EngineBase implements Engine {

    protected final Logger logger = LoggerFactory.getLogger("Engine");

    protected final Path runPath;

    protected final List<Runnable> shutdownListeners = new LinkedList<>();

    private RuntimeEnvironment runtimeEnvironment;

    protected EventBus eventBus;
    protected RegistryManager registryManager;
    protected EngineModManager modManager;
    protected CrashHandlerImpl crashHandler;

    private boolean initialized = false;
    private boolean running = false;
    private boolean markedTermination = false;

    protected EngineBase(Path runPath) {
        this.runPath = runPath;
    }

    @Override
    public Path getRunPath() {
        return runPath;
    }

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
    public RegistryManager getRegistryManager() {
        return registryManager;
    }

    @Override
    public ModManager getModManager() {
        return modManager;
    }

    @Override
    public CrashHandlerImpl getCrashHandler() {
        return crashHandler;
    }

    @Override
    public void initEngine() {
        if (initialized) {
            throw new IllegalStateException("Engine has been initialized.");
        }
        initialized = true;

        constructionStage();
        resourceStage();
        modStage();
        finishStage();
    }

    protected void constructionStage() {
        logger.info("Initializing engine!");

        initExceptionHandler();
        initEnvironment();
        printSystemInfo();

        eventBus = SimpleEventBus.builder().eventListenerFactory(AsmEventListenerFactory.create()).build();
        registryManager = new SimpleRegistryManager(new HashMap<>());
        modManager = new EngineModManager(this);
    }

    protected void resourceStage() {

    }

    protected void modStage() {
        modManager.initEngineDummyMod();

        loadMods();
    }

    protected void finishStage() {
        Registries.init(registryManager);
    }

    private void initExceptionHandler() {
        crashHandler = new CrashHandlerImpl(this, getRunPath().resolve("crashreports"));
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            logger.error("Caught unhandled exception!!! Engine will terminate!");
            getCrashHandler().crash(e);
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
        modManager.loadMods();

        Collection<ModContainer> loadedMods = modManager.getLoadedMods();
        logger.info("Loaded mods: [" + StringUtils.join(loadedMods.stream().map(modContainer -> modContainer.getId() + "(" + modContainer.getVersion() + ")").iterator(), ", ") + "]");
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
