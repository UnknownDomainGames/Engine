package nullengine.block.component;

import nullengine.component.Component;
import nullengine.event.block.cause.BlockInteractCause;
import nullengine.world.collision.RayTraceBlockHit;

/**
 * Call when the block is left clicked.
 */
public interface ClickBehavior extends Component {
    default boolean onClicked(RayTraceBlockHit blockHit, BlockInteractCause cause) {
        return false;
    }
}
