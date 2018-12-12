package unknowndomain.engine.world.chunk;

import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.collection.LongObjectMap;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.world.World;

import javax.annotation.Nonnull;
import java.util.Collection;

public class ChunkStorage {
    // should do the io operation to load chunk
    private final World world;

    private LongObjectMap<Chunk> chunks = new LongObjectHashMap<>();

    public ChunkStorage(World world) {
        this.world = world;
    }

    public Collection<Chunk> getChunks() {
        return chunks.values();
    }

    @Nonnull
    public Chunk getChunk(BlockPos pos) {
        return getChunkByChunkPos(pos.getX() >> 4, pos.getY() >> 4, pos.getZ() >> 4);
    }

    @Nonnull
    public Chunk getChunk(int x, int y, int z) {
        return getChunkByChunkPos(x >> 4, y >> 4, z >> 4);
    }

    @Nonnull
    public Chunk getChunkByChunkPos(int chunkX, int chunkY, int chunkZ) {
        long cp = getChunkPos(chunkX, chunkY, chunkZ);
        Chunk chunk = this.chunks.get(cp);
        if (chunk != null)
            return chunk;
        ChunkImpl c = new ChunkImpl(world);
//        c.data = decorateChunk();
        this.chunks.put(cp, c);
        return c;
    }

    public void touchChunk(@Nonnull BlockPos pos) {
//        long cp = getChunkPos(pos);
//        Chunk chunk = this.chunks.get(cp);
//        if (chunk != null) {
//            ChunkImpl c = new ChunkImpl(world);
////            c.data = decorateChunk();
//            this.chunks.put(cp, c);
//        }
    }

    public void discardChunk(@Nonnull BlockPos pos) {
//        long cp = getChunkPos(pos);
//        Chunk remove = this.chunks.remove(cp);
        // save this chunk?
    }

//    private int[] decorateChunk() {
//        int[] data = new int[16 * 16 * 16];
//        for (int x = 0; x < 16; x++) {
//            for (int z = 0; z < 16; z++) {
//                int y = 0;
//                data[x << 8 | y << 4 | z] = 2;
//            }
//        }
//        return data;
//    }

    private long getChunkPos(int chunkX, int chunkY, int chunkZ) {
        return (long) (chunkX << 42) | (chunkY << 21) | chunkZ;
    }
}
