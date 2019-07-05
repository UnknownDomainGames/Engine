package nullengine.client.rendering.block;

import nullengine.block.Block;
import nullengine.client.rendering.util.buffer.GLBuffer;
import nullengine.component.Component;
import nullengine.math.BlockPos;
import nullengine.util.Facing;
import nullengine.world.BlockGetter;

public interface BlockRenderer extends Component {

    default boolean isVisible() {
        return true;
    }

    boolean canRenderFace(BlockGetter world, BlockPos pos, Block block, Facing facing);

    boolean canRenderNeighborBlockFace(BlockGetter world, BlockPos pos, Block block, Facing facing);

    void generateMesh(Block block, BlockGetter world, BlockPos pos, GLBuffer buffer);

    void generateMesh(Block block, GLBuffer buffer);

    BlockRenderType getRenderType();
}
