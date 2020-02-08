package engine.block.component;

import engine.component.Component;
import engine.event.block.cause.BlockInteractCause;
import engine.world.hit.BlockHitResult;

/**
 * Call when the block is right clicked.
 */
public interface ActivateBehavior extends Component {
    default boolean onActivated(BlockHitResult blockHit, BlockInteractCause cause) {
        return false;
    }
}
