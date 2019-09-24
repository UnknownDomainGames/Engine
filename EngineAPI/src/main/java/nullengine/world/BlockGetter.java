package nullengine.world;

import nullengine.block.Block;
import nullengine.math.BlockPos;

import javax.annotation.Nonnull;

public interface BlockGetter {

    @Nonnull
    default Block getBlock(@Nonnull BlockPos pos) {
        return getBlock(pos.x(), pos.y(), pos.z());
    }

    @Nonnull
    Block getBlock(int x, int y, int z);

    default int getBlockId(@Nonnull BlockPos pos) {
        return getBlockId(pos.x(), pos.y(), pos.z());
    }

    int getBlockId(int x, int y, int z);

    default boolean isAirBlock(@Nonnull BlockPos pos) {
        return isAirBlock(pos.x(), pos.y(), pos.z());
    }

    boolean isAirBlock(int x, int y, int z);
}
