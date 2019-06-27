package nullengine.block.component;

import nullengine.block.Block;
import nullengine.component.Component;
import nullengine.math.BlockPos;
import nullengine.world.World;

public interface RandomTickListener extends Component {
    void onRandomTick(World world, BlockPos pos, Block block);
}
