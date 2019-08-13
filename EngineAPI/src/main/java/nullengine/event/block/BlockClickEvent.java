package nullengine.event.block;

import nullengine.block.Block;
import nullengine.entity.Entity;
import nullengine.event.Event;
import nullengine.math.BlockPos;
import nullengine.world.World;

public class BlockClickEvent implements Event {
    private final World world;
    private final Entity entity;
    private final BlockPos pos;
    private final Block block;

    public BlockClickEvent(World world, Entity entity, BlockPos pos, Block block) {
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
