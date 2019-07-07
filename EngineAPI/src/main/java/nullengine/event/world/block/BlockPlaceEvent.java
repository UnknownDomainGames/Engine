package nullengine.event.world.block;

import nullengine.block.Block;
import nullengine.event.world.block.cause.BlockChangeCause;
import nullengine.math.BlockPos;
import nullengine.world.World;

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
