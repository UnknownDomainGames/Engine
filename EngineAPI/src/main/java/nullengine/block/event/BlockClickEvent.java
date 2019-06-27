package nullengine.block.event;

import nullengine.entity.Entity;
import nullengine.block.Block;
import nullengine.event.Event;
import nullengine.math.BlockPos;

public class BlockClickEvent implements Event {
    private BlockPos blockPos;
    private Block block;
    private Entity entity;

    public BlockClickEvent(BlockPos blockPos, Block block, Entity entity) {
        this.blockPos = blockPos;
        this.block = block;
        this.entity = entity;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public Block getBlock() {
        return block;
    }

    public Entity getEntity() {
        return entity;
    }
}
