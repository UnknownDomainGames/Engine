package unknowndomain.engine.event.world.block;

import unknowndomain.engine.block.Block;
import unknowndomain.engine.entity.Entity;
import unknowndomain.engine.event.Event;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.world.World;

public class BlockClickEvent implements Event {
    private final World world;
    private final Entity entity;
    private final BlockPos pos;
    private final Block block;

    public BlockClickEvent(World world, Entity entity, BlockPos pos, Block block){
        this.world = world;
        this.entity = entity;
        this.pos = pos;
        this.block = block;
    }

    public World getWorld() {
        return world;
    }

    public Entity getEntity() {
        return entity;
    }

    public BlockPos getPos() {
        return pos;
    }

    public Block getBlock() {
        return block;
    }
}
