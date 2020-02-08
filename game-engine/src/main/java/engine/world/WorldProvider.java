package engine.world;

import engine.game.Game;
import engine.registry.Registrable;

import javax.annotation.Nonnull;
import java.nio.file.Path;

public interface WorldProvider extends Registrable<WorldProvider> {

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
