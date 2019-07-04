package nullengine.world;

import nullengine.block.Block;
import nullengine.entity.Entity;
import nullengine.game.Game;
import nullengine.math.BlockPos;
import nullengine.world.chunk.Chunk;
import nullengine.world.chunk.ChunkManager;
import nullengine.world.collision.WorldCollisionManager;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

/**
 * World instance, should spawn by {@link Game}
 */
public interface World extends BlockAccessor {
    Game getGame();

    WorldCollisionManager getCollisionManager();

    ChunkManager<World> getChunkManager();

    long getGameTick();

    List<Entity> getEntities();

    default Chunk getChunk(@Nonnull BlockPos pos) {
        return getChunk(pos.getX() >> 4, pos.getY() >> 4, pos.getZ() >> 4);
    }

    Chunk getChunk(int chunkX, int chunkY, int chunkZ);

    Block removeBlock(@Nonnull BlockPos pos);

    Collection<Chunk> getLoadedChunks();

    @Nonnull
    @Override
    default World getWorld() {
        return this;
    }

    WorldInfo getWorldInfo();

    interface Config {

    }
}
