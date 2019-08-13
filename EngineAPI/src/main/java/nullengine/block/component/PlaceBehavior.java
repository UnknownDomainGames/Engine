package nullengine.block.component;

import nullengine.block.Block;
import nullengine.component.Component;
import nullengine.event.block.cause.BlockChangeCause;
import nullengine.math.BlockPos;
import nullengine.world.World;

public interface PlaceBehavior extends Component {
    default boolean canPlace(World world, BlockPos blockPos, Block block, BlockChangeCause cause) {
        return true;
    }

    void onPlaced(World world, BlockPos blockPos, Block block, BlockChangeCause cause);
}
