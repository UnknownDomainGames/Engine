package unknowndomain.engine.game;

import unknowndomain.engine.client.resource.ResourcePath;
import unknowndomain.engine.component.RuntimeObject;
import unknowndomain.engine.world.World;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

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
public interface Game extends RuntimeObject, Runnable {

    GameContext getContext();

    World spawnWorld(World.Config config);

    boolean isTerminated();
}
