package engine.event.block;

import engine.block.state.BlockState;
import engine.event.Cancellable;
import engine.event.Event;
import engine.event.block.cause.BlockChangeCause;
import engine.math.BlockPos;
import engine.world.World;

public interface BlockChangeEvent extends Event {

    World getWorld();

    BlockPos getPos();

    BlockState getOldBlock();

    BlockState getNewBlock();

    BlockChangeCause getCause();

    class Base implements BlockChangeEvent {

        private final World world;
        private final BlockPos pos;
        private final BlockState oldBlock;
        private final BlockState newBlock;
        private final BlockChangeCause cause;

        private Base(World world, BlockPos pos, BlockState oldBlock, BlockState newBlock, BlockChangeCause cause) {
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

        public BlockState getOldBlock() {
            return oldBlock;
        }

        public BlockState getNewBlock() {
            return newBlock;
        }

        public BlockChangeCause getCause() {
            return cause;
        }
    }

    class Pre extends BlockChangeEvent.Base implements Cancellable {

        private boolean cancelled;

        protected Pre(World world, BlockPos pos, BlockState oldBlock, BlockState newBlock, BlockChangeCause cause) {
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
        protected Post(World world, BlockPos pos, BlockState oldBlock, BlockState newBlock, BlockChangeCause cause) {
            super(world, pos, oldBlock, newBlock, cause);
        }
    }
}
