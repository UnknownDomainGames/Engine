package nullengine.event.block;

import nullengine.block.Block;
import nullengine.event.action.InteractEvent;
import nullengine.event.block.cause.BlockInteractCause;
import nullengine.math.BlockPos;
import nullengine.util.Direction;
import nullengine.world.World;
import nullengine.world.collision.RayTraceBlockHit;
import org.joml.Vector3fc;

import java.util.Optional;

public abstract class BlockInteractEvent implements InteractEvent {
    private final RayTraceBlockHit blockHit;
    private final BlockInteractCause cause;

    public BlockInteractEvent(RayTraceBlockHit blockHit, BlockInteractCause cause) {
        this.blockHit = blockHit;
        this.cause = cause;
    }

    public World getWorld() {
        return blockHit.getWorld();
    }

    public BlockPos getPos() {
        return blockHit.getPos();
    }

    public Block getBlock() {
        return blockHit.getBlock();
    }

    public Direction getDirection() {
        return blockHit.getDirection();
    }

    public BlockInteractCause getCause() {
        return cause;
    }

    @Override
    public Optional<Vector3fc> getInteractionPoint() {
        return Optional.of(blockHit.getHitPoint());
    }

    public static final class Click extends BlockInteractEvent {

        public Click(RayTraceBlockHit blockHit, BlockInteractCause cause) {
            super(blockHit, cause);
        }
    }

    public static final class Activate extends BlockInteractEvent {

        public Activate(RayTraceBlockHit blockHit, BlockInteractCause cause) {
            super(blockHit, cause);
        }
    }
}
