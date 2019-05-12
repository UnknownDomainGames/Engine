package unknowndomain.engine.game;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unknowndomain.engine.Engine;
import unknowndomain.engine.event.EventBus;
import unknowndomain.engine.event.SimpleEventBus;
import unknowndomain.engine.event.asm.AsmEventListenerFactory;
import unknowndomain.engine.event.game.GameEvent;
import unknowndomain.engine.event.registry.RegistrationEvent;
import unknowndomain.engine.event.registry.RegistryConstructionEvent;
import unknowndomain.engine.registry.Registries;
import unknowndomain.engine.registry.Registry;
import unknowndomain.engine.registry.RegistryEntry;
import unknowndomain.engine.registry.RegistryManager;
import unknowndomain.engine.registry.impl.SimpleRegistryManager;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class GameBase implements Game {

    protected final Engine engine;

    protected final Logger logger = LoggerFactory.getLogger("Game");

    protected RegistryManager registryManager;

    protected EventBus eventBus;

    protected boolean terminated = false;
    protected boolean stopped = false;

    public GameBase(Engine engine) {
        this.engine = engine;
        this.eventBus = SimpleEventBus.builder().eventListenerFactory(AsmEventListenerFactory.create()).build();
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
        logger.info("Creating Registry Manager!");
        Map<Class<?>, Registry<?>> registries = Maps.newHashMap();
        Map<Class<?>, List<Pair<Class<? extends RegistryEntry>, BiConsumer<RegistryEntry, Registry>>>> afterRegistries = Maps.newHashMap();
        eventBus.post(new RegistryConstructionEvent(registries, afterRegistries));
        registryManager = new SimpleRegistryManager(Map.copyOf(registries), Map.copyOf(afterRegistries));
        logger.info("Registering!");
        eventBus.post(new RegistrationEvent.Start(registryManager));

        for (Registry<?> registry : registries.values())
            eventBus.post(new RegistrationEvent.Register<>(registry));

        logger.info("Finishing Registration!");
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
        eventBus.post(new GameEvent.Ready(this));
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
