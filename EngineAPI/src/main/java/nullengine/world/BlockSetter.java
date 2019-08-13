package nullengine.world;

import nullengine.block.Block;
import nullengine.event.block.cause.BlockChangeCause;
import nullengine.math.BlockPos;

import javax.annotation.Nonnull;

public interface BlockSetter {
    @Nonnull
    Block setBlock(@Nonnull BlockPos pos, @Nonnull Block block, @Nonnull BlockChangeCause cause);

    @Nonnull
    Block destoryBlock(@Nonnull BlockPos pos, @Nonnull BlockChangeCause cause);
}
