package nullengine.event.world.block;

import nullengine.block.Block;
import nullengine.event.Cancellable;
import nullengine.event.Event;
import nullengine.event.world.block.cause.BlockChangeCause;
import nullengine.math.BlockPos;
import nullengine.world.World;

public abstract class BlockChangeEvent implements Event {

    private final World world;
    private final BlockPos pos;
    private final Block oldBlock;
    private final Block newBlock;
    private final BlockChangeCause cause;
    private final BlockAction action;

    protected BlockChangeEvent(World world, BlockPos pos, Block oldBlock, Block newBlock, BlockAction action, BlockChangeCause cause) {
        this.world = world;
        this.pos = pos;
        this.oldBlock = oldBlock;
        this.newBlock = newBlock;
        this.action = action;
        this.cause = cause;
    }

    public BlockChangeCause getCause() {
        return cause;
    }

    public Block getOldBlock() {
        return oldBlock;
    }

    public Block getNewBlock() {
        return newBlock;
    }

    public World getWorld() {
        return world;
    }

    public BlockPos getPos() {
        return pos;
    }

    public BlockAction getAction() {
        return action;
    }

    public enum BlockAction{
        PLACE, DESTROY, REPLACE
    }

    public static class Pre extends BlockChangeEvent implements Cancellable {

        private boolean cancelled;

        public Pre(World world, BlockPos pos, Block oldBlock, Block newBlock, BlockAction action, BlockChangeCause cause) {
            super(world, pos, oldBlock, newBlock, action, cause);
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

    public static class Post extends BlockChangeEvent {
        public Post(World world, BlockPos pos, Block oldBlock, Block newBlock, BlockAction action, BlockChangeCause cause) {
            super(world, pos, oldBlock, newBlock, action, cause);
        }
    }
}
