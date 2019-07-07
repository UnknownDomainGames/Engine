package nullengine.event.world.block;

import nullengine.block.Block;
import nullengine.event.Cancellable;
import nullengine.event.Event;
import nullengine.event.world.block.cause.BlockChangeCause;
import nullengine.math.BlockPos;
import nullengine.world.World;

public class BlockReplaceEvent extends BlockChangeEvent {

    public BlockReplaceEvent(World world, BlockPos pos, Block oldBlock, Block newBlock, BlockChangeCause cause) {
        super(world, pos, oldBlock, newBlock, cause);
    }

    public static class Pre extends BlockReplaceEvent implements Cancellable {

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

    public static class Post extends BlockReplaceEvent {
        public Post(World world, BlockPos pos, Block oldBlock, Block newBlock, BlockChangeCause cause) {
            super(world, pos, oldBlock, newBlock, cause);
        }
    }
}
