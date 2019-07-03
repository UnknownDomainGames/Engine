package nullengine;

import com.google.common.collect.Maps;
import nullengine.event.EventBus;
import nullengine.event.SimpleEventBus;
import nullengine.event.asm.AsmEventListenerFactory;
import nullengine.event.engine.EngineEvent;
import nullengine.event.registry.RegistrationEvent;
import nullengine.event.registry.RegistryConstructionEvent;
import nullengine.mod.ModContainer;
import nullengine.mod.ModManager;
import nullengine.mod.dummy.DummyModContainer;
import nullengine.mod.impl.EngineModManager;
import nullengine.mod.init.ModInitializer;
import nullengine.registry.Registries;
import nullengine.registry.Registry;
import nullengine.registry.RegistryManager;
import nullengine.registry.impl.SimpleRegistryManager;
import nullengine.util.ClassPathUtils;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.SystemUtils.*;

public abstract class EngineBase implements Engine {

    protected final Logger logger = LoggerFactory.getLogger("Engine");

    protected final List<Runnable> shutdownListeners = new LinkedList<>();

    private RuntimeEnvironment runtimeEnvironment;

    protected EventBus eventBus;
    protected RegistryManager registryManager;

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
    public RegistryManager getRegistryManager() {
        return registryManager;
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

    protected void initRegistration() {
        // Registration Stage
        logger.info("Creating Registry Manager!");
        Map<Class<?>, Registry<?>> registries = Maps.newHashMap();
        eventBus.post(new RegistryConstructionEvent(registries));
        registryManager = new SimpleRegistryManager(Map.copyOf(registries));
        logger.info("Registering!");
        eventBus.post(new RegistrationEvent.Start(registryManager));

        for (Registry<?> registry : registries.values()) {
            eventBus.post(new RegistrationEvent.Register<>(registry));
        }

        logger.info("Finishing Registration!");
        eventBus.post(new RegistrationEvent.Finish(registryManager));

        Registries.init(registryManager);
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
        modManager = new EngineModManager(this);
        modManager.loadMods();

        Collection<ModContainer> loadedMods = modManager.getLoadedMods();
        logger.info("Loaded mods: [" + StringUtils.join(loadedMods.stream().map(modContainer -> modContainer.getId() + "(" + modContainer.getVersion() + ")").iterator(), ", ") + "]");

        ModInitializer initializer = new ModInitializer(this);
        for (ModContainer mod : loadedMods) {
            if (mod instanceof DummyModContainer) {
                continue;
            }
            initializer.init(mod);
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
