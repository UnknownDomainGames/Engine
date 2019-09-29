package nullengine.world.chunk;

import java.nio.file.Path;

public interface ChunkStorage {

    Path getStoragePath();

    Chunk load(int chunkX, int chunkY, int chunkZ);

    void save(Chunk chunk);
}
