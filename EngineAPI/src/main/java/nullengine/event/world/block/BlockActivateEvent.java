package nullengine.event.world.block;

import nullengine.block.Block;
import nullengine.entity.Entity;
import nullengine.event.Event;
import nullengine.math.BlockPos;
import nullengine.world.World;

public class BlockActivateEvent implements Event {
    private World world;
    private Entity entity;
    private BlockPos pos;
    private Block block;

    public BlockActivateEvent(World world, Entity entity, BlockPos pos, Block block) {
        this.block = block;
        this.entity = entity;
        this.pos = pos;
        this.world = world;
    }
}
