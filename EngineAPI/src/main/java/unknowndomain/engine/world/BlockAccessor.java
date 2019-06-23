package unknowndomain.engine.world;

import unknowndomain.engine.block.Block;
import unknowndomain.engine.event.world.block.cause.BlockChangeCause;
import unknowndomain.engine.math.BlockPos;

import javax.annotation.Nonnull;

public interface BlockAccessor {

    @Nonnull
    World getWorld();

    @Nonnull
    Block setBlock(@Nonnull BlockPos pos, @Nonnull Block block, @Nonnull BlockChangeCause cause);

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
