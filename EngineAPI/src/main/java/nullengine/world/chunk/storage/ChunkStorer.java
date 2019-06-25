package nullengine.world.chunk.storage;

import nullengine.world.World;
import nullengine.world.chunk.Chunk;

public interface ChunkStorer {

    World getWorld();

    Chunk load(int chunkX, int chunkY, int chunkZ);

    void save(Chunk chunk);
}
