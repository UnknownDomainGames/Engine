package nullengine.block.component;

import nullengine.block.Block;
import nullengine.component.Component;
import nullengine.math.BlockPos;
import nullengine.world.World;

/**
 * Call when the block is left clicked.
 */
public interface ClickBehavior extends Component {
    default boolean onClicked(World world, BlockPos pos, Block block) {
        return false;
    }
}
