package nullengine.event.world.block;

import nullengine.block.Block;
import nullengine.event.Event;
import nullengine.event.EventBase;
import nullengine.math.BlockPos;
import nullengine.world.World;

public abstract class BlockPlaceEvent extends BlockChangeEvent implements Event {
    public BlockPlaceEvent(World world, BlockPos pos, Block newBlock) {
        super(world, pos, null, newBlock);
    }

    public static class Pre extends BlockPlaceEvent {
        public Pre(World world, BlockPos pos, Block newBlock) {
            super(world, pos, newBlock);
        }
    }

    public static class Post extends BlockPlaceEvent {
        public Post(World world, BlockPos pos, Block newBlock) {
            super(world, pos, newBlock);
        }
    }
}
