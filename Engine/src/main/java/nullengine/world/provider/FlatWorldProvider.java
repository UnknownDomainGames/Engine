package nullengine.world.provider;

import nullengine.game.Game;
import nullengine.world.BaseWorldProvider;
import nullengine.world.World;
import nullengine.world.WorldCommon;
import nullengine.world.WorldCreationSetting;
import nullengine.world.gen.FlatChunkGenerator;
import nullengine.world.impl.FlatWorldCreationSetting;
import nullengine.world.storage.WorldCommonLoader;

import javax.annotation.Nonnull;
import java.nio.file.Path;

public class FlatWorldProvider extends BaseWorldProvider {
    @Nonnull
    @Override
    public World create(@Nonnull Game game, @Nonnull Path storagePath, @Nonnull String name, @Nonnull WorldCreationSetting creationSetting) {
        FlatWorldCreationSetting setting = (FlatWorldCreationSetting) creationSetting;
        return new WorldCommon(game, this, storagePath, creationSetting, new WorldCommonLoader(storagePath.resolve(name)), new FlatChunkGenerator(setting.getLayers()));
    }

    @Nonnull
    @Override
    public World load(@Nonnull Game game, @Nonnull Path storagePath) {
        return null;
    }
}
