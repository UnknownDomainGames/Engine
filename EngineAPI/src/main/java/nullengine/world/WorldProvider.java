package nullengine.world;

import nullengine.game.Game;
import nullengine.registry.RegistryEntry;

import javax.annotation.Nonnull;
import java.nio.file.Path;

public interface WorldProvider extends RegistryEntry<WorldProvider> {

    /**
     * @param game        The game of the world
     * @param name        Name of the world created
     * @param storagePath Storage path of world
     * @return
     */
    @Nonnull
    World create(@Nonnull Game game, @Nonnull Path storagePath, @Nonnull String name, @Nonnull WorldCreationSetting creationSetting);

    @Nonnull
    World load(@Nonnull Game game, @Nonnull Path storagePath);
}
