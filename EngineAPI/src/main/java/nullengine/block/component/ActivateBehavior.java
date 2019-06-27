package nullengine.block.component;

import nullengine.block.Block;
import nullengine.component.Component;
import nullengine.entity.Entity;
import nullengine.math.BlockPos;
import nullengine.world.World;

/**
 * Call when the block is right clicked.
 */
public interface ActivateBehavior extends Component {
    default boolean onActivated(World world, Entity entity, BlockPos pos, Block block) {
        return false;
    }
}
