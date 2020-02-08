package engine.block.component;

import engine.component.Component;
import engine.event.block.cause.BlockInteractCause;
import engine.world.hit.BlockHitResult;

/**
 * Call when the block is left clicked.
 */
public interface ClickBehavior extends Component {
    default boolean onClicked(BlockHitResult blockHit, BlockInteractCause cause) {
        return false;
    }
}
