package unknowndomain.engine.game;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import unknowndomain.engine.Engine;
import unknowndomain.engine.event.EventBus;
import unknowndomain.engine.event.game.GameStartEvent;
import unknowndomain.engine.event.game.GameTerminationEvent;
import unknowndomain.engine.event.registry.RegistrationEvent;
import unknowndomain.engine.event.registry.RegistryConstructionEvent;
import unknowndomain.engine.registry.Registries;
import unknowndomain.engine.registry.Registry;
import unknowndomain.engine.registry.RegistryManager;
import unknowndomain.engine.registry.impl.SimpleRegistryManager;

import javax.annotation.Nonnull;
import java.util.Map;

public abstract class GameBase implements Game {

    protected final Engine engine;

    protected final Logger logger;
    protected final Marker marker = MarkerFactory.getMarker("Game");

    protected RegistryManager registryManager;

    protected EventBus eventBus;

    protected boolean markedTermination = false;
    protected boolean terminated = false;

    public GameBase(Engine engine) {
        this.engine = engine;
        this.logger = engine.getLogger();
        this.eventBus = engine.getEventBus();
    }

    /**
     * Construct stage, collect mod and resource according to it option
     */
    protected void constructStage() {
    }

    /**
     * Register stage, collect all registerable things from mod here.
     */
    protected void registerStage() {
        // Registration Stage
        logger.info(marker, "Creating Registry Manager!");
        Map<Class<?>, Registry<?>> registries = Maps.newHashMap();
        eventBus.post(new RegistryConstructionEvent(registries));
        registryManager = new SimpleRegistryManager(Map.copyOf(registries));
        logger.info(marker, "Registering!");
        eventBus.post(new RegistrationEvent.Start(registryManager));

        for (Registry<?> registry : registries.values()) {
            eventBus.post(new RegistrationEvent.Register<>(registry));
        }

        logger.info(marker, "Finishing Registration!");
        eventBus.post(new RegistrationEvent.Finish(registryManager));

        Registries.init(registryManager);
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
        eventBus.post(new GameStartEvent.Post(this));
    }

    @Nonnull
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

    @Override
    public boolean isMarkedTermination() {
        return markedTermination;
    }

    @Override
    public void init() {
        logger.info(marker, "Initializing Game.");
        eventBus.post(new GameStartEvent.Pre(this));
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
        markedTermination = true;
        logger.info(marker, "Marked game terminated!");
        eventBus.post(new GameTerminationEvent.Marked(this));
    }

    protected void tryTerminate() {
        terminated = true;
    }

    @Override
    public boolean isTerminated() {
        return terminated;
    }

    // TODO: unload mod/resource
}
