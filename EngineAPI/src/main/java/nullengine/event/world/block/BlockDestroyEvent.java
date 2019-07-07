package nullengine.event.world.block;

import nullengine.block.Block;
import nullengine.event.Cancellable;
import nullengine.event.Event;
import nullengine.event.world.block.cause.BlockChangeCause;
import nullengine.math.BlockPos;
import nullengine.registry.Registries;
import nullengine.world.World;

public class BlockDestroyEvent extends BlockChangeEvent {

    public BlockDestroyEvent(World world, BlockPos pos, Block oldBlock, BlockChangeCause cause) {
        super(world, pos, oldBlock, Registries.getBlockRegistry().air(), cause);
    }

    public static class Pre extends BlockDestroyEvent implements Cancellable {

        private boolean cancelled;

        public Pre(World world, BlockPos pos, Block oldBlock, BlockChangeCause cause) {
            super(world, pos, oldBlock, cause);
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

    public static class Post extends BlockDestroyEvent{

        public Post(World world, BlockPos pos, Block oldBlock, BlockChangeCause cause) {
            super(world, pos, oldBlock, cause);
        }
    }
}
