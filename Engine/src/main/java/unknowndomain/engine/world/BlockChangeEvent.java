package unknowndomain.engine.world;

import unknowndomain.engine.block.Block;
import unknowndomain.engine.event.Event;
import unknowndomain.engine.math.BlockPos;

public abstract class BlockChangeEvent implements Event {

    private final World world;
    private final BlockPos pos;
    private final Block oldBlock;
    private final Block newBlock;

    protected BlockChangeEvent(World world, BlockPos pos, Block oldBlock, Block newBlock) {
        this.world = world;
        this.pos = pos;
        this.oldBlock = oldBlock;
        this.newBlock = newBlock;
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
        public Pre(World world, BlockPos pos, Block oldBlock, Block newBlock) {
            super(world, pos, oldBlock, newBlock);
        }
    }

    public static class Post extends BlockChangeEvent {
        public Post(World world, BlockPos pos, Block oldBlock, Block newBlock) {
            super(world, pos, oldBlock, newBlock);
        }
    }
}
