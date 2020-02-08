package engine.world;

import engine.block.Block;
import engine.event.block.cause.BlockChangeCause;
import engine.math.BlockPos;

import javax.annotation.Nonnull;

public interface BlockSetter {
    @Nonnull
    Block setBlock(@Nonnull BlockPos pos, @Nonnull Block block, @Nonnull BlockChangeCause cause);

    @Nonnull
    Block destroyBlock(@Nonnull BlockPos pos, @Nonnull BlockChangeCause cause);
}
