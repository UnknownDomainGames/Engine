package unknowndomain.engine.world;

import unknowndomain.engine.block.BlockObject;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.RuntimeObject;

public interface World extends RuntimeObject {
    Chunk getChunk(int x, int z);

    BlockObject getBlock(BlockPos pos);

    BlockObject setBlock(BlockPos pos, BlockObject block);
}
