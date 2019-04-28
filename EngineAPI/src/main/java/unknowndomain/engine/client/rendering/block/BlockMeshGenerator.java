package unknowndomain.engine.client.rendering.block;

import unknowndomain.engine.client.block.ClientBlock;
import unknowndomain.engine.client.rendering.util.buffer.GLBuffer;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.world.BlockAccessor;

public interface BlockMeshGenerator {

    void generate(ClientBlock block, BlockAccessor world, BlockPos pos, GLBuffer buffer);

    void generate(ClientBlock block, GLBuffer buffer);
}
