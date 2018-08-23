package unknowndomain.engine.game;

import unknowndomain.engine.GameContext;
import unknowndomain.engine.Prototype;
import unknowndomain.engine.RuntimeObject;
import unknowndomain.engine.client.resource.ResourcePath;
import unknowndomain.engine.mod.ModIdentifier;
import unknowndomain.engine.world.World;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * The game shares the same set of mod and resources pack manifest.
 * <p>The game manages the complex life cycle of mods, configs, worlds.</p>
 * <p>I used to decide to construct all the dependency (mod, config) before game instance creation.</p>
 * <p>But, its complexity makes me feel that it should manage all these by itself (after it create)</p>
 *
 * <p>
 * Each world should hold a separated thread
 */
public interface Game extends RuntimeObject, Prototype<RuntimeObject, Game>, Runnable {
    GameContext getContext();

    Collection<World> getWorlds();

    @Nullable
    World getWorld(String name);

//    World spawnWorld(); // TODO: spawn world by config

    boolean isTerminated();

    interface Config {
        ModIdentifier[] mods();

        ResourcePath[] resources();
    }


    // World spawnWorld(WorldConfig? config); // TODO: design this
}
