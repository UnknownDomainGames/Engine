package nullengine.event.world.block;

import nullengine.block.Block;
import nullengine.math.BlockPos;
import nullengine.world.World;
import org.joml.Vector3fc;

public abstract class BlockUseEvent extends BlockInteractEvent {
    public BlockUseEvent(World world, BlockPos pos, Block block, Vector3fc hit) {
        super(world, pos, block, hit);
    }

    public static class Pre extends BlockUseEvent {
        public Pre(World world, BlockPos pos, Block block, Vector3fc hit) {
            super(world, pos, block, hit);
        }
    }

    public static class Post extends BlockUseEvent {
        public Post(World world, BlockPos pos, Block block, Vector3fc hit) {
            super(world, pos, block, hit);
        }
    }
}
