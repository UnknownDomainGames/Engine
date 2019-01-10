package unknowndomain.engine.client.rendering.block;

import unknowndomain.engine.client.block.ClientBlock;
import unknowndomain.engine.client.rendering.util.BufferBuilder;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.util.Disposable;
import unknowndomain.engine.world.BlockAccessor;

public interface BlockRenderer extends Disposable {

    void render(ClientBlock block, BlockAccessor world, BlockPos pos, BufferBuilder buffer);

    void render(ClientBlock block, BufferBuilder buffer);
}
