package nullengine;

import nullengine.event.EventBus;
import nullengine.event.SimpleEventBus;
import nullengine.event.asm.AsmEventListenerFactory;
import nullengine.event.engine.EngineEvent;
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static java.lang.String.format;
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

        initEnvironment();
        printSystemInfo();
        initExceptionHandler();

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

    protected void initExceptionHandler() {
        crashHandler = new CrashHandlerImpl(this, getRunPath().resolve("crashreports"));
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            logger.error("Caught unhandled exception!!! Engine will terminate!");
            crashHandler.crash(e);
        });
        crashHandler.addReportDetail("Engine Version",
                builder -> builder.append(Platform.getVersion()));
        crashHandler.addReportDetail("Engine Side",
                builder -> builder.append(getSide()));
        crashHandler.addReportDetail("Runtime Environment",
                builder -> builder.append(getRuntimeEnvironment()));
        crashHandler.addReportDetail("Loaded Mods",
                builder -> {
                    if (modManager == null)
                        builder.append("[]");
                    else
                        builder.append("[").append(StringUtils.join(modManager.getLoadedMods().stream().map(modContainer -> modContainer.getId() + "(" + modContainer.getVersion() + ")").iterator(), ", ")).append("]");
                });
        crashHandler.addReportDetail("Operating System",
                builder -> builder.append(format("%s (%s) version %s", OS_NAME, OS_ARCH, OS_VERSION)));
        crashHandler.addReportDetail("Java Version",
                builder -> builder.append(format("%s (%s), %s", JAVA_VERSION, JAVA_VM_VERSION, JAVA_VERSION)));
        crashHandler.addReportDetail("JVM Information",
                builder -> builder.append(format("%s (%s), %s", JAVA_VM_NAME, JAVA_VM_INFO, JAVA_VM_VENDOR)));
        crashHandler.addReportDetail("Memory",
                builder -> {
                    Runtime runtime = Runtime.getRuntime();
                    long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
                    long totalMemory = runtime.totalMemory() / 1024 / 1024;
                    long maxMemory = runtime.maxMemory() / 1024 / 1024;
                    builder.append(format("%d MB / %d MB (Max: %d MB)", usedMemory, totalMemory, maxMemory));
                });
        crashHandler.addReportDetail("JVM Flags",
                builder -> {
                    RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
                    List<String> jvmFlags = runtimeMXBean.getInputArguments();
                    builder.append(format("(%s totals) %s", jvmFlags.size(), String.join(" ", jvmFlags)));
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
        logger.info("\tEngine Version: {}", Platform.getVersion());
        logger.info("\tEngine Side: {}", getSide());
        logger.info("\tRuntime Environment: {}", runtimeEnvironment);
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
        logger.info("\tJVM Flags ({} totals): {}", jvmFlags.size(), String.join(" ", jvmFlags));
        logger.info("------------------------------");
    }

    private void loadMods() {
        logger.info("Loading Mods.");
        modManager.loadMods();

        logger.info("Loaded Mods: [" + StringUtils.join(modManager.getLoadedMods().stream().map(modContainer -> modContainer.getId() + "(" + modContainer.getVersion() + ")").iterator() + "]", ", "));
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
