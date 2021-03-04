package engine.world.chunk;

import engine.world.World;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

/**
 * Chunk Column
 * <p>
 * This object contains a column of chunks in a World object.
 * Chunk Column is a collection of chunks which have the same x- and z-coordinate
 */
public class ChunkColumn {

    private final World world;
    private final int chunkX;
    private final int chunkZ;

    private Int2ObjectMap<Chunk> chunkCache = new Int2ObjectOpenHashMap<>();

    public ChunkColumn(World world, int chunkX, int chunkZ) {
        this.world = world;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    public Chunk getChunk(int chunkY) {
        if (chunkCache.containsKey(chunkY)) {
            return chunkCache.get(chunkY);
        }
        var chunk = world.getChunk(chunkX, chunkY, chunkZ);
        chunkCache.putIfAbsent(chunkY, chunk);
        return chunk;
    }
}
