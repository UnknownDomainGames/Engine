package nullengine.block.component;

import nullengine.component.Component;
import nullengine.event.block.cause.BlockInteractCause;
import nullengine.world.raytrace.RayTraceBlockHit;

/**
 * Call when the block is right clicked.
 */
public interface ActivateBehavior extends Component {
    default boolean onActivated(RayTraceBlockHit blockHit, BlockInteractCause cause) {
        return false;
    }
}
