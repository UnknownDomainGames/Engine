package unknowndomain.engine.world;

import unknowndomain.engine.block.Block;
import unknowndomain.engine.math.BlockPos;

import javax.annotation.Nonnull;

public interface BlockAccessor {

    @Nonnull
    Block setBlock(@Nonnull BlockPos pos, @Nonnull Block block);

    @Nonnull
    default Block getBlock(@Nonnull BlockPos pos) {
        return getBlock(pos.getX(), pos.getY(), pos.getZ());
    }

    @Nonnull
    Block getBlock(int x, int y, int z);
}
