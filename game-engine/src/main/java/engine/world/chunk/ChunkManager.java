package engine.world.chunk;

import engine.player.Player;
import engine.world.gen.ChunkGenerator;
import org.joml.Vector3dc;
import org.joml.Vector3ic;

import java.util.Collection;
import java.util.Optional;

public interface ChunkManager {
    Collection<Chunk> getLoadedChunks();

    default Optional<Chunk> getChunk(Vector3ic pos) {
        return getChunk(pos.x(), pos.y(), pos.z());
    }

    Optional<Chunk> getChunk(int x, int y, int z);

    default Chunk getOrLoadChunk(Vector3ic pos) {
        return getOrLoadChunk(pos.x(), pos.y(), pos.z());
    }

    Chunk getOrLoadChunk(int x, int y, int z);

    void unloadChunk(Chunk chunk);

    void unloadAll();

    void saveAll();

    ChunkGenerator getChunkGenerator();

    void handlePlayerJoin(Player player);

    void handlePlayerMove(Player player, Vector3dc prevPos);
}
