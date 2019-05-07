package unknowndomain.engine.event.world.block;

import unknowndomain.engine.block.Block;
import unknowndomain.engine.event.Event;
import unknowndomain.engine.event.world.block.cause.BlockChangeCause;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.world.World;

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
