package engine.world.chunk;

import engine.block.Block;
import engine.event.block.cause.BlockChangeCause;
import engine.math.BlockPos;
import engine.world.World;
import org.joml.Vector3ic;

import javax.annotation.Nonnull;

public interface Chunk {

    @Nonnull
    World getWorld();

    ChunkPos getPos();

    int getX();

    int getY();

    int getZ();

    @Nonnull
    Vector3ic getMin();

    @Nonnull
    Vector3ic getMax();

    @Nonnull
    Vector3ic getCenter();

    /**
     * Get block in a specific path
     *
     * @param x x-coordinate of the block related to chunk coordinate system
     * @param y y-coordinate of the block related to chunk coordinate system
     * @param z z-coordinate of the block related to chunk coordinate system
     * @return the block in the specified path
     */
    Block getBlock(int x, int y, int z);

    default Block getBlock(@Nonnull BlockPos pos) {
        return getBlock(pos.x(), pos.y(), pos.z());
    }

    int getBlockId(int x, int y, int z);

    default int getBlockId(@Nonnull BlockPos pos) {
        return getBlockId(pos.x(), pos.y(), pos.z());
    }

    Block setBlock(@Nonnull BlockPos pos, @Nonnull Block block, @Nonnull BlockChangeCause cause);

    boolean isAirChunk();
}
