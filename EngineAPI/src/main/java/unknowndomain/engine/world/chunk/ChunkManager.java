package unknowndomain.engine.world.chunk;

import unknowndomain.engine.player.Player;

public interface ChunkManager {
    boolean shouldChunkUnload(Chunk chunk, Player player);

    Chunk loadChunk(int x, int z);

    void unloadChunk(int x, int z);
}
