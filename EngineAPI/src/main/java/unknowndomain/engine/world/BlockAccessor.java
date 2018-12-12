package unknowndomain.engine.world;

import unknowndomain.engine.block.Block;
import unknowndomain.engine.math.BlockPos;

import javax.annotation.Nonnull;

public interface BlockAccessor {

    @Nonnull
    default Block getBlock(@Nonnull BlockPos pos) {
        return getBlock(pos.getX(), pos.getY(), pos.getZ());
    }

    @Nonnull
    Block getBlock(int x, int y, int z);

    @Nonnull
    default Block setBlock(@Nonnull BlockPos pos, @Nonnull Block block) {
        return setBlock(pos.getX(), pos.getY(), pos.getZ(), block);
    }

    @Nonnull
    Block setBlock(int x, int y, int z, @Nonnull Block block);
}
