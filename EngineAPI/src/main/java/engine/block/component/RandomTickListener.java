package engine.block.component;

import engine.block.Block;
import engine.component.Component;
import engine.math.BlockPos;
import engine.world.World;

public interface RandomTickListener extends Component {
    void onRandomTick(World world, BlockPos pos, Block block);
}
