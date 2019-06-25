package nullengine.client.block;

import nullengine.block.Block;
import nullengine.math.BlockPos;
import nullengine.util.Facing;
import nullengine.world.BlockAccessor;

public class AirClientBlock extends DefaultClientBlock {

    public AirClientBlock(Block block) {
        super(block);
    }

    @Override
    public boolean isVisible() {
        return false;
    }

    @Override
    public boolean canRenderNeighborBlockFace(BlockAccessor world, BlockPos pos, Facing facing) {
        return true;
    }
}
