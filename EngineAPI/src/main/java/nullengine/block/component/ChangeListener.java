package nullengine.block.component;

import nullengine.block.Block;
import nullengine.component.Component;
import nullengine.event.block.cause.BlockChangeCause;
import nullengine.math.BlockPos;
import nullengine.world.World;

public interface ChangeListener extends Component {
    void onChange(World world, BlockPos pos, Block block, BlockChangeCause cause);
}
