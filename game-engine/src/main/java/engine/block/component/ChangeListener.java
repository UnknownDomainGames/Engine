package engine.block.component;

import engine.block.Block;
import engine.component.Component;
import engine.event.block.cause.BlockChangeCause;
import engine.math.BlockPos;
import engine.world.World;

public interface ChangeListener extends Component {
    void onChange(World world, BlockPos pos, Block block, BlockChangeCause cause);
}
