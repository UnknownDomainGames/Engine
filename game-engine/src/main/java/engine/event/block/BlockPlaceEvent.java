package engine.event.block;

import engine.block.Block;
import engine.event.block.cause.BlockChangeCause;
import engine.math.BlockPos;
import engine.world.World;

public interface BlockPlaceEvent extends BlockChangeEvent {

    class Pre extends BlockChangeEvent.Pre implements BlockPlaceEvent {

        public Pre(World world, BlockPos pos, Block oldBlock, Block newBlock, BlockChangeCause cause) {
            super(world, pos, oldBlock, newBlock, cause);
        }
    }

    class Post extends BlockChangeEvent.Post implements BlockPlaceEvent {

        public Post(World world, BlockPos pos, Block oldBlock, Block newBlock, BlockChangeCause cause) {
            super(world, pos, oldBlock, newBlock, cause);
        }
    }
}
