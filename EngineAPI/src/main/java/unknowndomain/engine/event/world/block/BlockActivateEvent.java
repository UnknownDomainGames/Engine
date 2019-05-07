package unknowndomain.engine.event.world.block;

import unknowndomain.engine.block.Block;
import unknowndomain.engine.entity.Entity;
import unknowndomain.engine.event.Event;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.world.World;

public class BlockActivateEvent implements Event {
    private World world;
    private Entity entity;
    private BlockPos pos;
    private Block block;

    public BlockActivateEvent(World world, Entity entity, BlockPos pos, Block block){
        this.block = block;
        this.entity = entity;
        this.pos = pos;
        this.world = world;
    }
}
