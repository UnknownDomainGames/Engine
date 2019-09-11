package nullengine.world;

import nullengine.entity.Entity;
import nullengine.game.Game;
import nullengine.math.BlockPos;
import nullengine.world.chunk.Chunk;
import nullengine.world.collision.WorldCollisionManager;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.Collection;

/**
 * World instance, should spawn by {@link Game}
 */
public interface World extends BlockGetter, BlockSetter {

    Game getGame();

    WorldProvider getProvider();

    Path getStoragePath();

    WorldCreationSetting getCreationSetting();

    WorldSetting getSetting();

    WorldCollisionManager getCollisionManager();

    long getGameTick();

    Collection<Entity> getEntities();

    default Chunk getChunk(@Nonnull BlockPos pos) {
        return getChunk(pos.x() >> 4, pos.y() >> 4, pos.z() >> 4);
    }

    Chunk getChunk(int chunkX, int chunkY, int chunkZ);

    Collection<Chunk> getLoadedChunks();

    @Nonnull
    @Override
    default World getWorld() {
        return this;
    }
}
