package nullengine.event.block;

import nullengine.block.Block;
import nullengine.event.Cancellable;
import nullengine.event.block.cause.BlockChangeCause;
import nullengine.math.BlockPos;
import nullengine.world.World;

public interface BlockReplaceEvent extends BlockChangeEvent {

    class Pre extends BlockChangeEvent.Pre implements BlockReplaceEvent, Cancellable {

        private boolean cancelled;

        public Pre(World world, BlockPos pos, Block oldBlock, Block newBlock, BlockChangeCause cause) {
            super(world, pos, oldBlock, newBlock, cause);
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public void setCancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }
    }

    class Post extends BlockChangeEvent.Post implements BlockReplaceEvent {
        public Post(World world, BlockPos pos, Block oldBlock, Block newBlock, BlockChangeCause cause) {
            super(world, pos, oldBlock, newBlock, cause);
        }
    }
}
