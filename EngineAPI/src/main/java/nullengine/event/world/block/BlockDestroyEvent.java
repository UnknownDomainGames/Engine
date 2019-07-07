package nullengine.event.world.block;

import nullengine.block.Block;
import nullengine.event.Cancellable;
import nullengine.event.world.block.cause.BlockChangeCause;
import nullengine.math.BlockPos;
import nullengine.world.World;

public interface BlockDestroyEvent extends BlockChangeEvent {

    class Pre extends BlockChangeEvent.Pre implements BlockDestroyEvent, Cancellable {

        public Pre(World world, BlockPos pos, Block oldBlock, Block newBlock, BlockChangeCause cause) {
            super(world, pos, oldBlock, newBlock, cause);
        }
    }

    class Post extends BlockChangeEvent.Post implements BlockDestroyEvent {

        public Post(World world, BlockPos pos, Block oldBlock, Block newBlock, BlockChangeCause cause) {
            super(world, pos, oldBlock, newBlock, cause);
        }
    }
}
