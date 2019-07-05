package nullengine.world;

import nullengine.block.Block;
import nullengine.math.BlockPos;

import javax.annotation.Nonnull;

public interface BlockGetter {

    @Nonnull
    World getWorld();

    @Nonnull
    default Block getBlock(@Nonnull BlockPos pos) {
        return getBlock(pos.getX(), pos.getY(), pos.getZ());
    }

    @Nonnull
    Block getBlock(int x, int y, int z);

    default int getBlockId(@Nonnull BlockPos pos) {
        return getBlockId(pos.getX(), pos.getY(), pos.getZ());
    }

    int getBlockId(int x, int y, int z);
}
