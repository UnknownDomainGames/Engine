package engine.game;

import engine.Engine;
import engine.entity.Entity;
import engine.event.EventBus;
import engine.player.Player;
import engine.player.Profile;
import engine.world.World;
import engine.world.WorldCreationSetting;
import engine.world.exception.WorldLoadException;
import engine.world.exception.WorldNotExistsException;

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

    Path getStoragePath();

    @Nonnull
    GameData getData();

    @Nonnull
    Path getStorageBasePath();

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

    boolean isReadyToPlay();

    boolean isMarkedTermination();

    boolean isTerminated();

    @Nonnull
    EventBus getEventBus();

    // ########## Internal Methods ##########

    void doUnloadWorld(World world);
}
