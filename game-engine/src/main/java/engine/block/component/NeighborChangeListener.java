package engine.block.component;

import engine.block.Block;
import engine.component.Component;
import engine.event.block.cause.BlockChangeCause;
import engine.math.BlockPos;
import engine.util.Direction;
import engine.world.World;

public interface NeighborChangeListener extends Component {
    void onNeighborChanged(World world, BlockPos pos, Block block, Direction direction, BlockPos neighborPos, Block neighbor, BlockChangeCause cause);
}
