package unknowndomain.engine.world.chunk.storage;

import unknowndomain.engine.world.World;
import unknowndomain.engine.world.chunk.Chunk;

public interface ChunkStorer {

    World getWorld();

    Chunk load(int chunkX, int chunkY, int chunkZ);

    void save(Chunk chunk);
}
