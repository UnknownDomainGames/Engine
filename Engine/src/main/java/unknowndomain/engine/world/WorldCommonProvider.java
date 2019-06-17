package unknowndomain.engine.world;

import org.apache.commons.lang3.Validate;
import unknowndomain.engine.game.Game;
import unknowndomain.engine.world.gen.ChunkGenerator;
import unknowndomain.engine.world.storage.WorldCommonLoader;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.Objects;

public class WorldCommonProvider extends BaseWorldProvider<WorldCommon> {

    private ChunkGenerator chunkGenerator;

    public WorldCommonProvider(){

    }

    public void setChunkGenerator(ChunkGenerator chunkGenerator) {
        this.chunkGenerator = chunkGenerator;
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
