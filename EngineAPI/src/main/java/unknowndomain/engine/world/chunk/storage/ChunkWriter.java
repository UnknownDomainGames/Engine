package unknowndomain.engine.world.chunk.storage;

import unknowndomain.engine.world.World;
import unknowndomain.engine.world.chunk.Chunk;

public interface ChunkWriter {

    void writeChunk(World world, Chunk chunk);

    void flush();
}
