package nullengine.world.chunk;

import nullengine.player.Player;

import java.util.Collection;

public interface ChunkManager {
    boolean shouldChunkUnload(Chunk chunk, Player player);

    Chunk getChunk(int x, int y, int z);

    Chunk getOrLoadChunk(int x, int y, int z);

    Chunk loadChunk(int x, int y, int z);

    void unloadChunk(int x, int y, int z);

    void unloadChunk(Chunk chunk);

    void unloadAll();

    void saveAll();

    Collection<Chunk> getLoadedChunks();
}
