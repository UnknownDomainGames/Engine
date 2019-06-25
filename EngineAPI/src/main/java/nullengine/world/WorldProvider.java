package nullengine.world;

import nullengine.game.Game;
import nullengine.registry.RegistryEntry;

import javax.annotation.Nonnull;
import java.nio.file.Path;

public interface WorldProvider<T extends World> extends RegistryEntry<WorldProvider<T>> {

    /**
     * @param game        The game of the world
     * @param name        Name of the world created
     * @param storagePath Storage path of ALL worlds
     * @return
     */
    @Nonnull
    T create(@Nonnull Game game, @Nonnull String name, @Nonnull Path storagePath);
}
