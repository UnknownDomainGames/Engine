package nullengine.game;

import nullengine.Engine;
import nullengine.entity.Entity;
import nullengine.event.EventBus;
import nullengine.player.Player;
import nullengine.player.Profile;
import nullengine.world.World;
import nullengine.world.WorldCreationSetting;
import nullengine.world.exception.WorldLoadException;
import nullengine.world.exception.WorldNotExistsException;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;

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

    @Nonnull
    Engine getEngine();

    @Nonnull
    GameData getData();

    @Nonnull
    Path getStoragePath();

    @Nonnull
    Player joinPlayer(Profile profile, Entity controlledEntity);

    @Nonnull
    Collection<Player> getPlayers();

    @Nonnull
    World createWorld(@Nonnull String providerName, @Nonnull String name, @Nonnull WorldCreationSetting config);

    @Nonnull
    World loadWorld(@Nonnull String name) throws WorldLoadException, WorldNotExistsException;

    void unloadWorld(@Nonnull String name);

    Optional<World> getWorld(@Nonnull String name);

    void init();

    void terminate();

    boolean isMarkedTermination();

    boolean isTerminated();

    @Nonnull
    EventBus getEventBus();

    // ########## Internal Methods ##########

    void doUnloadWorld(World world);
}
