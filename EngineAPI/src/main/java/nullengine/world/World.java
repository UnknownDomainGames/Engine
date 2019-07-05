package nullengine.world;

import nullengine.block.Block;
import nullengine.entity.Entity;
import nullengine.event.world.block.cause.BlockChangeCause;
import nullengine.game.Game;
import nullengine.math.BlockPos;
import nullengine.world.chunk.Chunk;
import nullengine.world.collision.WorldCollisionManager;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

/**
 * World instance, should spawn by {@link Game}
 */
public interface World extends BlockGetter {

    Game getGame();

    WorldCollisionManager getCollisionManager();

    long getGameTick();

    List<Entity> getEntities();

    default Chunk getChunk(@Nonnull BlockPos pos) {
        return getChunk(pos.getX() >> 4, pos.getY() >> 4, pos.getZ() >> 4);
    }

    Chunk getChunk(int chunkX, int chunkY, int chunkZ);

    @Nonnull
    Block setBlock(@Nonnull BlockPos pos, @Nonnull Block block, @Nonnull BlockChangeCause cause);

    Block removeBlock(@Nonnull BlockPos pos, @Nonnull BlockChangeCause cause);

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
