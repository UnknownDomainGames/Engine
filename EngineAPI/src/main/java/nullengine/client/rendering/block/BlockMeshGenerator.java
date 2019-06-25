package nullengine.client.rendering.block;

import nullengine.client.block.ClientBlock;
import nullengine.client.rendering.util.buffer.GLBuffer;
import nullengine.math.BlockPos;
import nullengine.world.BlockAccessor;

public interface BlockMeshGenerator {

    void generate(ClientBlock block, BlockAccessor world, BlockPos pos, GLBuffer buffer);

    void generate(ClientBlock block, GLBuffer buffer);
}
