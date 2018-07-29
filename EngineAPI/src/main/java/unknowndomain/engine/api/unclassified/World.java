package unknowndomain.engine.api.unclassified;

import unknowndomain.engine.api.math.BlockPos;
import unknowndomain.engine.api.world.Chunk;

public interface World extends RuntimeObject {
    Chunk getChunk(int x, int z);

    BlockObject getBlock(BlockPos pos);

    BlockObject setBlock(BlockPos pos, BlockObject block);
}
