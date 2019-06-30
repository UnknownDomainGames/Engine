package nullengine.event.world.block;

import nullengine.block.Block;
import nullengine.event.Event;
import nullengine.event.world.block.cause.BlockChangeCause;
import nullengine.math.BlockPos;
import nullengine.world.World;

public class BlockDestroyEvent implements Event {

    private final World world;
    private final BlockPos pos;
    private final Block oldBlock;
    private final BlockChangeCause cause;

    public BlockDestroyEvent(World world, BlockPos pos, Block oldBlock, BlockChangeCause cause) {
        this.world = world;
        this.pos = pos;
        this.oldBlock = oldBlock;
        this.cause = cause;
    }
}
