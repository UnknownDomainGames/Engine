package engine.world;

import engine.block.state.BlockState;
import engine.math.BlockPos;

import javax.annotation.Nonnull;

public interface BlockGetter {

    @Nonnull
    default BlockState getBlock(@Nonnull BlockPos pos) {
        return getBlock(pos.x(), pos.y(), pos.z());
    }

    @Nonnull
    BlockState getBlock(int x, int y, int z);

    default boolean isAirBlock(@Nonnull BlockPos pos) {
        return isAirBlock(pos.x(), pos.y(), pos.z());
    }

    boolean isAirBlock(int x, int y, int z);
}
