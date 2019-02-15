package unknowndomain.engine.client.rendering.block;

import unknowndomain.engine.client.block.ClientBlock;
import unknowndomain.engine.client.rendering.util.BufferBuilder;
import unknowndomain.engine.client.rendering.util.buffer.GLBuffer;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.util.Disposable;
import unknowndomain.engine.world.BlockAccessor;

public interface BlockMeshGenerator extends Disposable {

    void generate(ClientBlock block, BlockAccessor world, BlockPos pos, BufferBuilder buffer);

    void generate(ClientBlock block, BufferBuilder buffer);

    void generate(ClientBlock block, BlockAccessor world, BlockPos pos, GLBuffer buffer);

    void generate(ClientBlock block, GLBuffer buffer);
}
