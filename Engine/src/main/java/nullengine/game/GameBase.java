package nullengine.game;

import com.google.common.collect.Maps;
import nullengine.Engine;
import nullengine.event.EventBus;
import nullengine.event.game.GameStartEvent;
import nullengine.event.game.GameTerminationEvent;
import nullengine.event.registry.RegistrationEvent;
import nullengine.event.registry.RegistryConstructionEvent;
import nullengine.registry.Registries;
import nullengine.registry.Registry;
import nullengine.registry.RegistryManager;
import nullengine.registry.impl.SimpleRegistryManager;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

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
