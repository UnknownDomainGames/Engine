package engine.event.block;

import engine.block.state.BlockState;
import engine.event.action.InteractEvent;
import engine.event.block.cause.BlockInteractCause;
import engine.math.BlockPos;
import engine.util.Direction;
import engine.world.World;
import engine.world.hit.BlockHitResult;
import org.joml.Vector3fc;

import java.util.Optional;

public abstract class BlockInteractEvent implements InteractEvent {
    private final BlockHitResult blockHit;
    private final BlockInteractCause cause;

    public BlockInteractEvent(BlockHitResult blockHit, BlockInteractCause cause) {
        this.blockHit = blockHit;
        this.cause = cause;
    }

    public World getWorld() {
        return blockHit.getWorld();
    }

    public BlockPos getPos() {
        return blockHit.getPos();
    }

    public BlockState getBlock() {
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

        public Click(BlockHitResult blockHit, BlockInteractCause cause) {
            super(blockHit, cause);
        }
    }

    public static final class Activate extends BlockInteractEvent {

        public Activate(BlockHitResult blockHit, BlockInteractCause cause) {
            super(blockHit, cause);
        }
    }
}
