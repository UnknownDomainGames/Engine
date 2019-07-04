package nullengine.world;

import nullengine.block.Block;
import nullengine.event.Event;
import nullengine.event.world.block.cause.BlockInteractCause;
import nullengine.math.BlockPos;
import org.joml.Vector3fc;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface BlockAccessor {
    @Nonnull
    World getWorld();

    @Nonnull
    Block setBlock(@Nonnull BlockPos pos, @Nonnull Block block);

    boolean interactBlock(@Nonnull BlockPos pos, @Nonnull Block block, Vector3fc localPos);

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
