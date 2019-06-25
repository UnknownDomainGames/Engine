package nullengine.world.chunk.storage;

import nullengine.world.World;
import nullengine.world.chunk.Chunk;

public interface ChunkReader {
    Chunk readChunk(World world, int chunkX, int chunkZ);

}
