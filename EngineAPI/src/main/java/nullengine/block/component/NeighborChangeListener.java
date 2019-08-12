package nullengine.block.component;

import nullengine.block.Block;
import nullengine.component.Component;
import nullengine.event.world.block.cause.BlockChangeCause;
import nullengine.math.BlockPos;
import nullengine.util.Direction;
import nullengine.world.World;

public interface NeighborChangeListener extends Component {
    void onNeighborChanged(World world, BlockPos pos, Block block, Direction face, BlockPos neighborPos, Block neighbor, BlockChangeCause cause);
}
