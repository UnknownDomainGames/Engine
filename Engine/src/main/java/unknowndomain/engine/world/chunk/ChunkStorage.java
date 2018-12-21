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
        long chunkIndex = getChunkIndex(chunkX, chunkY, chunkZ);
        return this.chunks.getOrDefault(chunkIndex, empty);
    }

    public Chunk getOrLoadChunk(ChunkPos chunkPos) {
        long chunkIndex = getChunkIndex(chunkPos.getX(), chunkPos.getY(), chunkPos.getZ());
        Chunk chunk = chunks.get(chunkIndex);
        if (chunk == null) {
            chunk = new ChunkImpl(world);
            chunks.put(chunkIndex, chunk);
            world.getGame().getContext().post(new ChunkLoadEvent(world, chunkPos, chunk));
        }
        return chunk;
    }

    public void touchChunk(@Nonnull BlockPos pos) {
//        long cp = getChunkIndex(pos);
//        Chunk chunk = this.chunks.get(cp);
//        if (chunk != null) {
//            ChunkImpl c = new ChunkImpl(world);
////            c.data = decorateChunk();
//            this.chunks.put(cp, c);
//        }
    }

    public void discardChunk(@Nonnull BlockPos pos) {
//        long cp = getChunkIndex(pos);
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

    private static int maxPositiveChunkPos = (1 << 20) - 1;

    protected long getChunkIndex(int chunkX, int chunkY, int chunkZ) {
        return abs(chunkX, maxPositiveChunkPos) << 42 | abs(chunkY, maxPositiveChunkPos) << 21 | abs(chunkZ, maxPositiveChunkPos);
    }

    private static int abs(int value, int maxPositiveValue) {
        if (value >= 0) {
            return value;
        } else {
            return (value & maxPositiveValue) + maxPositiveValue + 1;
        }
    }

    public static void main(String[] args) {
        System.out.println(abs(maxPositiveChunkPos, maxPositiveChunkPos)); // 1048575
        System.out.println(abs(1, maxPositiveChunkPos)); // 1
        System.out.println(abs(0, maxPositiveChunkPos)); // 0
        System.out.println(abs(-1, maxPositiveChunkPos)); // 2097151
        System.out.println(abs(-maxPositiveChunkPos, maxPositiveChunkPos)); // 1048577
        System.out.println(abs(-maxPositiveChunkPos - 1, maxPositiveChunkPos)); // 1048576
    }
}
