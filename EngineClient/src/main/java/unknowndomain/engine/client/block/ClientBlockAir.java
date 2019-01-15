package unknowndomain.engine.client.block;

import unknowndomain.engine.block.Block;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.util.Facing;
import unknowndomain.engine.world.BlockAccessor;

public class ClientBlockAir extends ClientBlockDefault {

    public ClientBlockAir(Block block) {
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
