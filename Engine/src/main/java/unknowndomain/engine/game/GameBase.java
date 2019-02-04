package unknowndomain.engine.game;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unknowndomain.engine.Engine;
import unknowndomain.engine.event.AsmEventBus;
import unknowndomain.engine.event.EventBus;
import unknowndomain.engine.event.game.GameReadyEvent;
import unknowndomain.engine.event.mod.RegistrationFinishEvent;
import unknowndomain.engine.event.mod.RegistrationStartEvent;
import unknowndomain.engine.event.mod.RegistryConstructionEvent;
import unknowndomain.engine.mod.ModContainer;
import unknowndomain.engine.mod.ModManager;
import unknowndomain.engine.mod.impl.DefaultModManager;
import unknowndomain.engine.mod.util.ModCollector;
import unknowndomain.engine.registry.Registry;
import unknowndomain.engine.registry.RegistryManager;
import unknowndomain.engine.registry.impl.SimpleRegistryManager;
import unknowndomain.game.Blocks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;

public abstract class GameBase implements Game {

    protected final Engine engine;

    protected final Logger logger = LoggerFactory.getLogger("Game");

    private ModManager modManager;
    private RegistryManager registryManager;

    private EventBus eventBus;

    private boolean terminated = false;

    protected GameContext context;

    public GameBase(Engine engine) {
        this.engine = engine;
        this.eventBus = new AsmEventBus();
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
        eventBus.post(new RegistryConstructionEvent(registries));
        registryManager = new SimpleRegistryManager(Map.copyOf(registries));
        logger.info("Registering!");
        eventBus.post(new RegistrationStartEvent(registryManager));
        logger.info("Finishing Registration!");
        eventBus.post(new RegistrationFinishEvent(registryManager));

        this.context = new GameContext(registryManager, eventBus, Blocks.AIR);
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
        eventBus.post(new GameReadyEvent(this, context));
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
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

//        Platform.getLogger().info("Initializing Mods!");
//        getContext().post(new EngineEvent.ModInitializationEvent(this));
//        Platform.getLogger().info("Finishing Construction!");
//        getContext().post(new EngineEvent.ModConstructionFinish(this));
    }

    @Override
    public GameContext getContext() {
        return context;
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public boolean isTerminated() {
        return terminated;
    }

    @Override
    public void run() {
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
    public void terminate() {
        terminated = true;
        // TODO: unload mod/resource here
    }
}
