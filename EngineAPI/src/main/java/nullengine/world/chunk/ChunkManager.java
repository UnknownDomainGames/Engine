package nullengine.world.chunk;

import nullengine.player.Player;
import nullengine.world.World;

import java.util.Collection;

public interface ChunkManager<T extends World> {
    boolean shouldChunkUnload(Chunk chunk, Player player);

    Chunk loadChunk(int x, int y, int z);

    void unloadChunk(int x, int y, int z);

    T getWorld();

    Collection<Chunk> getChunks();
}
