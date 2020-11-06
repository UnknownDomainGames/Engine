package engine.block.component;

import engine.block.state.BlockState;
import engine.component.Component;
import engine.event.block.cause.BlockChangeCause;
import engine.math.BlockPos;
import engine.util.Direction;
import engine.world.World;

public interface NeighborChangeListener extends Component {
    void onNeighborChanged(World world, BlockPos pos, BlockState block, Direction direction, BlockPos neighborPos, BlockState neighbor, BlockChangeCause cause);
}
