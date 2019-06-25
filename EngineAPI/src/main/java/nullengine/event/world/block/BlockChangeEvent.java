package nullengine.event.world.block;

import nullengine.block.Block;
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

    protected BlockChangeEvent(World world, BlockPos pos, Block oldBlock, Block newBlock, BlockChangeCause cause) {
        this.world = world;
        this.pos = pos;
        this.oldBlock = oldBlock;
        this.newBlock = newBlock;
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

    public static class Pre extends BlockChangeEvent {
        public Pre(World world, BlockPos pos, Block oldBlock, Block newBlock, BlockChangeCause cause) {
            super(world, pos, oldBlock, newBlock, cause);
        }
    }

    public static class Post extends BlockChangeEvent {
        public Post(World world, BlockPos pos, Block oldBlock, Block newBlock, BlockChangeCause cause) {
            super(world, pos, oldBlock, newBlock, cause);
        }
    }
}
