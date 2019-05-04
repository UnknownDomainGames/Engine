package unknowndomain.engine.game;

import org.slf4j.Logger;
import unknowndomain.engine.Engine;
import unknowndomain.engine.event.EventBus;
import unknowndomain.engine.registry.RegistryManager;
import unknowndomain.engine.world.World;

import javax.annotation.Nonnull;

/**
 * The game shares the same set of mod and resources pack manifest.
 * <p>
 * The game manages the complex life cycle of mods, configs, worlds.
 * </p>
 * <p>
 * I used to decide to construct all the dependency (mod, config) before game
 * instance creation.
 * </p>
 * <p>
 * But, its complexity makes me feel that it should manage all these by itself
 * (after it parse)
 * </p>
 *
 * <p>
 * Each world should hold a separated thread
 */
public interface Game {

    Engine getEngine();

    World spawnWorld(World.Config config);

    void init();

    void terminate();

    void terminateNow();

    boolean isTerminated();

    boolean isStopped();

    @Nonnull
    EventBus getEventBus();

    @Nonnull
    RegistryManager getRegistryManager();

    @Nonnull
    Logger getLogger();
}
