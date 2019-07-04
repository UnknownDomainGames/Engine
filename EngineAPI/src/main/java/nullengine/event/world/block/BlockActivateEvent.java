package nullengine.event.world.block;

import nullengine.block.Block;
import nullengine.event.Event;
import nullengine.math.BlockPos;
import nullengine.world.World;
import org.joml.Vector3fc;

public abstract class BlockActivateEvent extends BlockInteractEvent {
    public BlockActivateEvent(World world, BlockPos pos, Block block, Vector3fc hit) {
        super(world, pos, block, hit);
    }

    public static class Pre extends BlockActivateEvent {
        public Pre(World world, BlockPos pos, Block block, Vector3fc hit) {
            super(world, pos, block, hit);
        }
    }

    public static class Post extends BlockActivateEvent {

        public Post(World world, BlockPos pos, Block block, Vector3fc hit) {
            super(world, pos, block, hit);
        }
    }
}