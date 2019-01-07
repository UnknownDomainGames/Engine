package unknowndomain.engine.world.chunk;

import unknowndomain.engine.block.Block;
import unknowndomain.engine.entity.Entity;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.world.World;

import javax.annotation.Nonnull;
import java.util.List;

public interface Chunk {

    int DEFAULT_X_SIZE = 16;
    int DEFAULT_Y_SIZE = 16;
    int DEFAULT_Z_SIZE = 16;

    int CHUNK_BLOCK_POS_BIT = 4;

    int MAX_BLOCK_POS = 0xf;

    World getWorld();

    int getChunkX();

    int getChunkY();

    int getChunkZ();

    @Nonnull
    List<Entity> getEntities();

    /**
     * Get block in a specific path
     *
     * @param x x-coordinate of the block related to chunk coordinate system
     * @param y y-coordinate of the block related to chunk coordinate system
     * @param z z-coordinate of the block related to chunk coordinate system
     * @return the block in the specified path
     */
    Block getBlock(int x, int y, int z);

    default Block getBlock(BlockPos pos) {
        return getBlock(pos.getX(), pos.getY(), pos.getZ());
    }

    int getBlockId(int x, int y, int z);

    default int getBlockId(BlockPos pos) {
        return getBlockId(pos.getX(), pos.getY(), pos.getZ());
    }

    Block setBlock(BlockPos pos, Block block);

    boolean isAirChunk();
}
