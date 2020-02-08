package engine.world.chunk;

import java.util.Collection;
import java.util.Optional;

public interface ChunkManager {
    Collection<Chunk> getLoadedChunks();

    default Optional<Chunk> getChunk(ChunkPos pos) {
        return getChunk(pos.x(), pos.y(), pos.z());
    }

    Optional<Chunk> getChunk(int x, int y, int z);

    default Chunk getOrLoadChunk(ChunkPos pos) {
        return getOrLoadChunk(pos.x(), pos.y(), pos.z());
    }

    Chunk getOrLoadChunk(int x, int y, int z);

    void unloadChunk(Chunk chunk);

    void unloadAll();

    void saveAll();
}
