package engine.event.block;

import engine.block.Block;
import engine.event.Cancellable;
import engine.event.block.cause.BlockChangeCause;
import engine.math.BlockPos;
import engine.world.World;

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
