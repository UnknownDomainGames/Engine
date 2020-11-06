package engine.block.component;

import engine.block.state.BlockState;
import engine.component.Component;
import engine.event.block.cause.BlockChangeCause;
import engine.math.BlockPos;
import engine.world.World;

public interface PlaceBehavior extends Component {
    default boolean canPlace(World world, BlockPos blockPos, BlockState block, BlockChangeCause cause) {
        return true;
    }

    void onPlaced(World world, BlockPos blockPos, BlockState block, BlockChangeCause cause);
}
