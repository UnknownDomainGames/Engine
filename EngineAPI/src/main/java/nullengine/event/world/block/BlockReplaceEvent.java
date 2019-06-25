package nullengine.event.world.block;

import nullengine.block.Block;
import nullengine.event.Event;
import nullengine.event.world.block.cause.BlockChangeCause;
import nullengine.math.BlockPos;
import nullengine.world.World;

public class BlockReplaceEvent implements Event {

    private final World world;
    private final BlockPos pos;
    private final Block oldBlock;
    private final Block newBlock;
    private final BlockChangeCause cause;

    public BlockReplaceEvent(World world, BlockPos pos, Block oldBlock, Block newBlock, BlockChangeCause cause) {
        this.world = world;
        this.pos = pos;
        this.oldBlock = oldBlock;
        this.newBlock = newBlock;
        this.cause = cause;
    }
}
