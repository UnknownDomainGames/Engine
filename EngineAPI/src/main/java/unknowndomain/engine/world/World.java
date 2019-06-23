package unknowndomain.engine.world;

import unknowndomain.engine.block.Block;
import unknowndomain.engine.entity.Entity;
import unknowndomain.engine.event.world.block.cause.BlockChangeCause;
import unknowndomain.engine.game.Game;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.world.chunk.Chunk;
import unknowndomain.engine.world.collision.WorldCollisionManager;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

/**
 * World instance, should spawn by {@link unknowndomain.engine.game.Game}
 */
public interface World extends BlockAccessor {

    Game getGame();

    WorldCollisionManager getCollisionManager();

    long getGameTick();

    List<Entity> getEntities();

    default Chunk getChunk(@Nonnull BlockPos pos) {
        return getChunk(pos.getX() >> 4, pos.getY() >> 4, pos.getZ() >> 4);
    }

    Chunk getChunk(int chunkX, int chunkY, int chunkZ);

    Block removeBlock(@Nonnull BlockPos pos, BlockChangeCause cause);

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
