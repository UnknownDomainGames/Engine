package nullengine.world;

import nullengine.game.Game;
import nullengine.world.gen.ChunkGenerator;
import nullengine.world.storage.WorldCommonLoader;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.Objects;

public class WorldCommonProvider extends BaseWorldProvider<WorldCommon> {

    private ChunkGenerator chunkGenerator;

    public WorldCommonProvider() {

    }

    public WorldCommonProvider setChunkGenerator(ChunkGenerator chunkGenerator) {
        this.chunkGenerator = chunkGenerator;
        return this;
    }

    public ChunkGenerator getChunkGenerator(){
        return chunkGenerator;
    }

    @Nonnull
    @Override
    public WorldCommon create(@Nonnull Game game, @Nonnull String name, @Nonnull Path storagePath) {
        Objects.requireNonNull(game);
        Validate.notEmpty(name);
        Objects.requireNonNull(storagePath);
        return new WorldCommon(game, new WorldCommonLoader(storagePath.resolve(name)), chunkGenerator);
    }
}
