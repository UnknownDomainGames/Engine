package nullengine.event.world.block;

import nullengine.block.Block;
import nullengine.event.Event;
import nullengine.math.BlockPos;
import nullengine.world.World;

public abstract class BlockDestroyEvent extends BlockChangeEvent implements Event {
    public BlockDestroyEvent(World world, BlockPos pos, Block oldBlock) {
        super(world, pos, oldBlock, null);
    }

    public static class Pre extends BlockDestroyEvent {
        public Pre(World world, BlockPos pos, Block oldBlock) {
            super(world, pos, oldBlock);
        }
    }

    public static class Post extends BlockDestroyEvent {
        public Post(World world, BlockPos pos, Block oldBlock) {
            super(world, pos, oldBlock);
        }
    }
}
