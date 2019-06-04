package unknowndomain.engine.world.chunk.storage;

import unknowndomain.engine.world.World;
import unknowndomain.engine.world.chunk.Chunk;

public interface ChunkReader {
    Chunk readChunk(World world, int chunkX, int chunkZ);

}
