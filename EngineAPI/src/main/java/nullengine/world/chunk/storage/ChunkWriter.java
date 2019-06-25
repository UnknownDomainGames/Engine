package nullengine.world.chunk.storage;

import nullengine.world.World;
import nullengine.world.chunk.Chunk;

public interface ChunkWriter {

    void writeChunk(World world, Chunk chunk);

    void flush();
}
