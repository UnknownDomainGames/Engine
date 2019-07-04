package nullengine.event.world.block;

import nullengine.block.Block;
import nullengine.event.Event;
import nullengine.math.BlockPos;
import nullengine.world.World;

public abstract class BlockReplaceEvent extends BlockChangeEvent {
    public BlockReplaceEvent(World world, BlockPos pos, Block oldBlock, Block newBlock) {
        super(world, pos, oldBlock, newBlock);
    }

    public static class Pre extends BlockReplaceEvent {
        public Pre(World world, BlockPos pos, Block oldBlock, Block newBlock) {
            super(world, pos, oldBlock, newBlock);
        }
    }

    public static class Post extends BlockReplaceEvent {
        public Post(World world, BlockPos pos, Block oldBlock, Block newBlock) {
            super(world, pos, oldBlock, newBlock);
        }
    }
}
