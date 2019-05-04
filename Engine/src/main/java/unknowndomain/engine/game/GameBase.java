package unknowndomain.engine.game;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unknowndomain.engine.Engine;
import unknowndomain.engine.event.EventBus;
import unknowndomain.engine.event.SimpleEventBus;
import unknowndomain.engine.event.asm.AsmEventListenerFactory;
import unknowndomain.engine.event.game.GameEvent;
import unknowndomain.engine.event.registry.RegisterEvent;
import unknowndomain.engine.event.registry.RegistrationFinishEvent;
import unknowndomain.engine.event.registry.RegistrationStartEvent;
import unknowndomain.engine.event.registry.RegistryConstructionEvent;
import unknowndomain.engine.mod.ModContainer;
import unknowndomain.engine.mod.ModManager;
import unknowndomain.engine.mod.impl.DefaultModManager;
import unknowndomain.engine.mod.util.ModCollector;
import unknowndomain.engine.registry.Registry;
import unknowndomain.engine.registry.RegistryEntry;
import unknowndomain.engine.registry.RegistryManager;
import unknowndomain.engine.registry.impl.SimpleRegistryManager;
import unknowndomain.engine.util.RuntimeEnvironment;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class GameBase implements Game {

    protected final Engine engine;
    protected final GameDefinition definition;

    protected final Logger logger = LoggerFactory.getLogger("Game");

    protected ModManager modManager;
    protected RegistryManager registryManager;

    protected EventBus eventBus;

    protected boolean terminated = false;
    protected boolean stopped = false;

    public GameBase(Engine engine, GameDefinition definition) {
        this.engine = engine;
        this.definition = definition;
        this.eventBus = SimpleEventBus.builder().eventListenerFactory(AsmEventListenerFactory.create()).build();
    }

    /**
     * Construct stage, collect mod and resource according to it option
     */
    protected void constructStage() {
        constructMods();
    }

    /**
     * Register stage, collect all registerable things from mod here.
     */
    protected void registerStage() {
        // Registration Stage
        logger.info("Creating Registry Manager!");
        Map<Class<?>, Registry<?>> registries = Maps.newHashMap();
        Map<Class<?>, List<Pair<Class<? extends RegistryEntry>, BiConsumer<RegistryEntry, Registry>>>> afterRegistries = Maps.newHashMap();
        eventBus.post(new RegistryConstructionEvent(registries, afterRegistries));
        registryManager = new SimpleRegistryManager(Map.copyOf(registries), Map.copyOf(afterRegistries));
        logger.info("Registering!");
        eventBus.post(new RegistrationStartEvent(registryManager));

        for (Registry<?> registry : registries.values())
            eventBus.post(new RegisterEvent<>(registry));

        logger.info("Finishing Registration!");
        eventBus.post(new RegistrationFinishEvent(registryManager));
    }

    /**
     * let mod and resource related module load resources.
     */
    protected void resourceStage() {

    }

    /**
     * final stage of the
     */
    protected void finishStage() {
        eventBus.post(new GameEvent.Ready(this));
    }

    private void constructMods() {
        logger.info("Loading Mods!");
        modManager = new DefaultModManager();

        Path modFolder = Paths.get("mods");
        if (!Files.exists(modFolder)) {
            try {
                Files.createDirectory(modFolder);
            } catch (IOException e) {
                logger.warn(e.getMessage(), e);
            }
        }

        try {
            Collection<ModContainer> modContainers = modManager.loadMod(ModCollector.createFolderModCollector(modFolder));
            modContainers.forEach(modContainer -> eventBus.register(modContainer.getInstance()));
            modContainers.forEach(modContainer -> logger.info("Loaded mod: {}", modContainer.getModId()));

            loadDevEnvMod();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

//        Platform.getLogger().info("Initializing Mods!");
//        getContext().post(new EngineEvent.ModInitializationEvent(this));
//        Platform.getLogger().info("Finishing Construction!");
//        getContext().post(new EngineEvent.ModConstructionFinish(this));
    }

    private void loadDevEnvMod() {
        if (engine.getRuntimeEnvironment() != RuntimeEnvironment.MOD_DEVELOPMENT)
            return;

        Path modPath = findModInClassPath();
        if (modPath == null)
            return;

        ModContainer modContainer = modManager.loadMod(modPath);
        eventBus.register(modContainer.getInstance());
        logger.info("Loaded mod: {}", modContainer.getModId());
    }

    private Path findModInClassPath() {
        for (String path : SystemUtils.JAVA_CLASS_PATH.split(";")) {
            if (Files.exists(Path.of(path, "metadata.json"))) {
                return Path.of(path);
            }
        }
        return null;
    }

    @Override
    public Engine getEngine() {
        return engine;
    }

    @Nonnull
    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Nonnull
    @Override
    public RegistryManager getRegistryManager() {
        return registryManager;
    }

    @Nonnull
    @Override
    public GameDefinition getDefinition() {
        return definition;
    }

    @Nonnull
    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public boolean isTerminated() {
        return terminated;
    }

    @Override
    public void init() {
        constructStage();
        registerStage();
        resourceStage();
        finishStage();

        // TODO: loop to check if we need to gc the world

        // for (WorldCommon worldCommon : internalWorlds) {
        // worldCommon.stop();
        // }
    }

    @Override
    public synchronized void terminate() {
        terminated = true;
        logger.info("Marked game terminated!");
    }

    @Override
    public void terminateNow() {
        tryTerminate();
    }

    protected void tryTerminate() {
        stopped = true;
    }

    @Override
    public boolean isStopped() {
        return stopped;
    }

    // TODO: unload mod/resource
}
