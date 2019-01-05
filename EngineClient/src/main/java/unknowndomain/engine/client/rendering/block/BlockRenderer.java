package unknowndomain.engine.client.rendering.block;

import unknowndomain.engine.block.Block;
import unknowndomain.engine.client.rendering.util.BufferBuilder;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.util.ChunkCache;
import unknowndomain.engine.util.Disposable;

public interface BlockRenderer extends Disposable {

    void render(Block block, ChunkCache chunkCache, BlockPos pos, BufferBuilder buffer);

    void render(Block block, BufferBuilder buffer);
}
