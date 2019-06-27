package nullengine.block.event;

import nullengine.block.Block;
import nullengine.entity.Entity;
import nullengine.event.Event;
import nullengine.event.world.block.cause.BlockChangeCause;
import nullengine.math.BlockPos;

public class BlockPlaceEvent implements Event {
    private Entity entity;
    private BlockPos blockPos;
    private Block oldBlock;
    private Block newBlock;
    private BlockChangeCause cause;

    public BlockPlaceEvent(Entity entity, BlockPos blockPos, Block oldBlock, Block newBlock, BlockChangeCause cause) {
        this.entity = entity;
        this.blockPos = blockPos;
        this.oldBlock = oldBlock;
        this.newBlock = newBlock;
        this.cause = cause;
    }

    public Entity getEntity() {
        return entity;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public Block getOldBlock() {
        return oldBlock;
    }

    public Block getNewBlock() {
        return newBlock;
    }

    public BlockChangeCause getCause() {
        return cause;
    }
}
