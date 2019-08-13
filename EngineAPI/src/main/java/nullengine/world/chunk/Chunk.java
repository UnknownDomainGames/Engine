package nullengine.world.chunk;

import nullengine.block.Block;
import nullengine.entity.Entity;
import nullengine.event.block.cause.BlockChangeCause;
import nullengine.math.BlockPos;
import nullengine.world.World;
import org.joml.Vector3fc;

import javax.annotation.Nonnull;
import java.util.List;

public interface Chunk {

    @Nonnull
    World getWorld();

    int getChunkX();

    int getChunkY();

    int getChunkZ();

    @Nonnull
    Vector3fc getMin();

    @Nonnull
    Vector3fc getMax();

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

    default Block getBlock(@Nonnull BlockPos pos) {
        return getBlock(pos.getX(), pos.getY(), pos.getZ());
    }

    int getBlockId(int x, int y, int z);

    default int getBlockId(@Nonnull BlockPos pos) {
        return getBlockId(pos.getX(), pos.getY(), pos.getZ());
    }

    Block setBlock(@Nonnull BlockPos pos, @Nonnull Block block, @Nonnull BlockChangeCause cause);

    boolean isAirChunk();
}
