package unknowndomain.engine.world;

import unknowndomain.engine.RuntimeObject;
import unknowndomain.engine.block.Block;
import unknowndomain.engine.math.BlockPos;

public interface World extends RuntimeObject {
    Chunk getChunk(int x, int z);

    Block getBlock(BlockPos pos);

    Block setBlock(BlockPos pos, Block block);
}
