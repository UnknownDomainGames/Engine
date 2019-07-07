package nullengine.event.world.block;

import nullengine.block.Block;
import nullengine.event.Cancellable;
import nullengine.event.Event;
import nullengine.event.world.block.cause.BlockChangeCause;
import nullengine.math.BlockPos;
import nullengine.registry.Registries;
import nullengine.world.World;

public class BlockPlaceEvent extends BlockChangeEvent {

    public BlockPlaceEvent(World world, BlockPos pos, Block newBlock, BlockChangeCause cause) {
        super(world,pos, Registries.getBlockRegistry().air(), newBlock, cause);
    }

    public static class Pre extends BlockPlaceEvent implements Cancellable {

        private boolean cancelled;

        public Pre(World world, BlockPos pos, Block newBlock, BlockChangeCause cause) {
            super(world, pos, newBlock, cause);
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

    public static class Post extends BlockPlaceEvent{

        public Post(World world, BlockPos pos, Block newBlock, BlockChangeCause cause) {
            super(world, pos, newBlock, cause);
        }
    }
}
