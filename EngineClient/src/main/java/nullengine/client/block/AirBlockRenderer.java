package nullengine.client.block;

import nullengine.block.Block;
import nullengine.client.rendering.block.DefaultBlockRenderer;
import nullengine.math.BlockPos;
import nullengine.util.Direction;
import nullengine.world.BlockGetter;

public class AirBlockRenderer extends DefaultBlockRenderer {

    public AirBlockRenderer() {
    }

    @Override
    public boolean isVisible() {
        return false;
    }

    @Override
    public boolean canRenderFace(BlockGetter world, BlockPos pos, Block block, Direction direction) {
        return false;
    }

    @Override
    public boolean canRenderNeighborBlockFace(BlockGetter world, BlockPos pos, Block block, Direction direction) {
        return true;
    }
}
