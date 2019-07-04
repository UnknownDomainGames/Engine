package nullengine.event.world.block;

import nullengine.block.Block;
import nullengine.event.EventBase;
import nullengine.math.BlockPos;
import nullengine.world.World;

public abstract class BlockChangeEvent extends EventBase.Cancellable {
    private final World world;
    private final BlockPos pos;
    private final Block oldBlock;
    private final Block newBlock;

    public BlockChangeEvent(World world, BlockPos pos, Block oldBlock, Block newBlock) {
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
}
