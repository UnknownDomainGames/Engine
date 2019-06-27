package nullengine.block.component;

import nullengine.block.Block;
import nullengine.component.Component;
import nullengine.event.world.block.cause.BlockChangeCause;
import nullengine.math.BlockPos;
import nullengine.util.Facing;
import nullengine.world.World;

public interface
NeighborChangeListener extends Component {
    void onNeighborChange(World world, BlockPos pos, Block block, Facing face, BlockPos neighborPos, Block neighbor, BlockChangeCause cause);
}
