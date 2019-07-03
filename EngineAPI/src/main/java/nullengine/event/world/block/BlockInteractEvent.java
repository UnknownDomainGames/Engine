package nullengine.event.world.block;

import org.joml.Vector3fc;

import nullengine.block.Block;
import nullengine.event.Cancellable;
import nullengine.event.Event;
import nullengine.event.world.block.cause.BlockInteractCause;
import nullengine.math.BlockPos;
import nullengine.util.Facing;
import nullengine.world.World;

public abstract class BlockInteractEvent implements Event, Cancellable {
    private boolean isCancelled;

    @Override
    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    private World world;
    private Block block;
    /**
     * World space block
     */
    private BlockPos position;
    /**
     * The hit vector should be in block local space
     */
    private Vector3fc hit;

    private Facing face;

    /**
     * The cause contains the reason & the type of the interact.
     */
    private BlockInteractCause cause;

    public Block getBlock() {
        return block;
    }

    public World getWorld() {
        return world;
    }

    public Vector3fc getHit() {
        return hit;
    }

    public BlockPos getPosition() {
        return position;
    }

    public BlockInteractCause getCause() {
        return cause;
    }

    public Facing getFace() {
        return face;
    }

    public static class Pre extends BlockInteractEvent {

        public Pre(World world, Block block, BlockPos position, Vector3fc hit, Facing facing, BlockInteractCause cause) {
            super(world, block, position, hit, facing, cause);
        }
    }

    public static class Post extends BlockInteractEvent {

        public Post(World world, Block block, BlockPos position, Vector3fc hit, Facing facing,
                BlockInteractCause cause) {
            super(world, block, position, hit, facing, cause);
        }
    }

    public BlockInteractEvent(World world, Block block, BlockPos position, Vector3fc hit, Facing facing,
            BlockInteractCause cause) {
        this.world = world;
        this.block = block;
        this.position = position;
        this.hit = hit;
        this.face = facing;
        this.cause = cause;
    }
}