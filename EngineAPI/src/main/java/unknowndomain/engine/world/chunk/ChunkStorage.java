package unknowndomain.engine.world.chunk;

import unknowndomain.engine.world.World;

public interface ChunkStorage {

    World getWorld();

    Chunk load(int chunkX, int chunkY, int chunkZ);

    void save(Chunk chunk);
}
