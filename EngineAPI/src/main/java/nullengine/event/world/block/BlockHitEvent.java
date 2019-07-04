package nullengine.event.world.block;

import nullengine.block.Block;
import nullengine.event.Cancellable;
import nullengine.event.Event;
import nullengine.math.BlockPos;
import nullengine.world.World;
import org.joml.Vector3fc;

public abstract class BlockHitEvent extends BlockInteractEvent implements Event, Cancellable {
    protected BlockHitEvent(World world, BlockPos pos, Block block, Vector3fc hit) {
        super(world, pos, block, hit);
    }

    public static class Pre extends BlockHitEvent {
        public Pre(World world, BlockPos pos, Block block, Vector3fc hit) {
            super(world, pos, block, hit);
        }
    }

    public static class Post extends BlockHitEvent {
        public Post(World world, BlockPos pos, Block block, Vector3fc hit) {
            super(world, pos, block, hit);
        }
    }
}
