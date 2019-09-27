package nullengine.block.component;

import nullengine.component.Component;
import nullengine.event.block.cause.BlockInteractCause;
import nullengine.world.hit.BlockHitResult;

/**
 * Call when the block is left clicked.
 */
public interface ClickBehavior extends Component {
    default boolean onClicked(BlockHitResult blockHit, BlockInteractCause cause) {
        return false;
    }
}
