package unknowndomain.engine.world.chunk.storage;

import unknowndomain.engine.world.World;
import unknowndomain.engine.world.chunk.Chunk;

public interface ChunkLoader {
    Chunk loadChunk(World world, int chunkX, int chunkZ);

    void saveChunk(World world, Chunk chunk);

    void flush();
}
