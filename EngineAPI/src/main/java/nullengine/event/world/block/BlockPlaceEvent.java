package nullengine.event.world.block;

import nullengine.block.Block;
import nullengine.event.Event;
import nullengine.event.world.block.cause.BlockChangeCause;
import nullengine.math.BlockPos;
import nullengine.world.World;

public class BlockPlaceEvent implements Event {

    private final World world;
    private final BlockPos pos;
    private final Block newBlock;
    private final BlockChangeCause cause;

    public BlockPlaceEvent(World world, BlockPos pos, Block newBlock, BlockChangeCause cause) {
        this.world = world;
        this.pos = pos;
        this.newBlock = newBlock;
        this.cause = cause;
    }
}
