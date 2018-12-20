package unknowndomain.engine.world.chunk;

import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.collection.LongObjectMap;
import unknowndomain.engine.event.world.chunk.ChunkLoadEvent;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.math.ChunkPos;
import unknowndomain.engine.world.World;

import javax.annotation.Nonnull;
import java.util.Collection;

public class ChunkStorage {

    // should do the io operation to load chunk
    private final World world;

    private final Chunk empty;

    private LongObjectMap<Chunk> chunks = new LongObjectHashMap<>();

    public ChunkStorage(World world) {
        this.world = world;
        this.empty = new ChunkEmpty(world);
    }

    public Collection<Chunk> getChunks() {
        return chunks.values();
    }

    @Nonnull
    public Chunk getChunkByBlockPos(BlockPos pos) {
        return getChunk(pos.getX() >> 4, pos.getY() >> 4, pos.getZ() >> 4);
    }

    @Nonnull
    public Chunk getChunkByBlockPos(int x, int y, int z) {
        return getChunk(x >> 4, y >> 4, z >> 4);
    }

    @Nonnull
    public Chunk getChunk(int chunkX, int chunkY, int chunkZ) {
        long chunkPosIndex = getChunkPos(chunkX, chunkY, chunkZ);
        return this.chunks.getOrDefault(chunkPosIndex, empty);
    }

    public Chunk getOrLoadChunk(ChunkPos chunkPos) {
        long chunkPosIndex = getChunkPos(chunkPos.getX(), chunkPos.getY(), chunkPos.getZ());
        Chunk chunk = chunks.get(chunkPosIndex);
        if (chunk == null) {
            chunk = new ChunkImpl(world);
            chunks.put(chunkPosIndex, chunk);
            world.getGame().getContext().post(new ChunkLoadEvent(world, chunkPos, chunk));
        }
        return chunk;
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

    private long getChunkPos(int chunkX, int chunkY, int chunkZ) { //FIXME: Unsupported negative int.
        return ((long) chunkX << 42) & ((1L << 63) - 1) | ((long) chunkY << 21) & ((1L << 42) - 1) | (long) chunkZ & ((1 << 21) - 1);
    }
}
