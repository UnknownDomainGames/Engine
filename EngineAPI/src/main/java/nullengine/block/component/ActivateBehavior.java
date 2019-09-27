package nullengine.block.component;

import nullengine.component.Component;
import nullengine.event.block.cause.BlockInteractCause;
import nullengine.world.hit.BlockHitResult;

/**
 * Call when the block is right clicked.
 */
public interface ActivateBehavior extends Component {
    default boolean onActivated(BlockHitResult blockHit, BlockInteractCause cause) {
        return false;
    }
}
