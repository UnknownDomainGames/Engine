package nullengine.world.chunk;

import nullengine.player.Player;

import java.util.Collection;

public interface ChunkManager {
    boolean shouldChunkUnload(Chunk chunk, Player player);

    Chunk getChunkIfPresent(int x, int y, int z);

    Chunk getOrLoadChunk(int x, int y, int z);

    Chunk loadChunk(int x, int y, int z);

    void unloadChunk(int x, int y, int z);

    Collection<Chunk> getLoadedChunks();
}
