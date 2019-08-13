package nullengine.event.block;

import nullengine.block.Block;
import nullengine.event.Cancellable;
import nullengine.event.Event;
import nullengine.event.block.cause.BlockChangeCause;
import nullengine.math.BlockPos;
import nullengine.world.World;

public interface BlockChangeEvent extends Event {

    World getWorld();

    BlockPos getPos();

    Block getOldBlock();

    Block getNewBlock();

    BlockChangeCause getCause();

    class Base implements BlockChangeEvent {

        private final World world;
        private final BlockPos pos;
        private final Block oldBlock;
        private final Block newBlock;
        private final BlockChangeCause cause;

        private Base(World world, BlockPos pos, Block oldBlock, Block newBlock, BlockChangeCause cause) {
            this.world = world;
            this.pos = pos;
            this.oldBlock = oldBlock;
            this.newBlock = newBlock;
            this.cause = cause;
        }

        public World getWorld() {
            return world;
        }

        public BlockPos getPos() {
            return pos;
        }

        public Block getOldBlock() {
            return oldBlock;
        }

        public Block getNewBlock() {
            return newBlock;
        }

        public BlockChangeCause getCause() {
            return cause;
        }
    }

    class Pre extends BlockChangeEvent.Base implements Cancellable {

        private boolean cancelled;

        protected Pre(World world, BlockPos pos, Block oldBlock, Block newBlock, BlockChangeCause cause) {
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

    class Post extends BlockChangeEvent.Base {
        protected Post(World world, BlockPos pos, Block oldBlock, Block newBlock, BlockChangeCause cause) {
            super(world, pos, oldBlock, newBlock, cause);
        }
    }
}
