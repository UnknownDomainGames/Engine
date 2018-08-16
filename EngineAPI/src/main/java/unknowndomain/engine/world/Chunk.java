package unknowndomain.engine.world;

import unknowndomain.engine.Entity;
import unknowndomain.engine.GameContext;
import unknowndomain.engine.RuntimeObject;
import unknowndomain.engine.block.Block;
import unknowndomain.engine.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

public interface Chunk extends RuntimeObject {
    /**
     * Get block in a specific location
     *
     * @param x x-coordinate of the block related to chunk coordinate system
     * @param y y-coordinate of the block related to chunk coordinate system
     * @param z z-coordinate of the block related to chunk coordinate system
     * @return the block in the specified location
     */
    default Block getBlock(int x, int y, int z) {
        return getBlock(new BlockPos(x, y, z));
    }

    Collection<Block> getRuntimeBlock();

    List<Entity> getEntities();

    Block getBlock(BlockPos pos);

    /**
     * Set block in a specific location
     *
     * @param x x-coordinate of the block related to chunk coordinate system
     * @param y y-coordinate of the block related to chunk coordinate system
     * @param z z-coordinate of the block related to chunk coordinate system
     */
    default Block setBlock(int x, int y, int z, Block destBlock) {
        return setBlock(new BlockPos(x, y, z), destBlock);
    }

    Block setBlock(BlockPos pos, Block destBlock);

    int DEFAULT_X_SIZE = 16;
    int DEFAULT_Y_SIZE = 256;
    int DEFAULT_Z_SIZE = 16;

    default int getXSize() {
        return DEFAULT_X_SIZE;
    }

    default int getYSize() {
        return DEFAULT_Y_SIZE;
    }

    default int getZSize() {
        return DEFAULT_Z_SIZE;
    }

    interface Provider {
        Collection<Chunk> getChunks();

        @Nonnull
        Chunk getChunk(@Nonnull GameContext gameContext, @Nonnull BlockPos pos);
    }
}
