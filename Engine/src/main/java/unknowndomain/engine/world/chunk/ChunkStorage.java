package unknowndomain.engine.world.chunk;

import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.collection.LongObjectMap;
import unknowndomain.engine.event.world.chunk.ChunkLoadEvent;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
        return getChunk(pos.getX() >> Chunk.CHUNK_BLOCK_POS_BIT, pos.getY() >> Chunk.CHUNK_BLOCK_POS_BIT, pos.getZ() >> Chunk.CHUNK_BLOCK_POS_BIT);
    }

    @Nullable // FIXME:
    public Chunk getChunkByBlockPos(int x, int y, int z) {
        return getChunk(x >> Chunk.CHUNK_BLOCK_POS_BIT, y >> Chunk.CHUNK_BLOCK_POS_BIT, z >> Chunk.CHUNK_BLOCK_POS_BIT);
    }

    @Nonnull
    public Chunk getChunk(int chunkX, int chunkY, int chunkZ) {
        long chunkIndex = getChunkIndex(chunkX, chunkY, chunkZ);
        return this.chunks.get(chunkIndex);
    }

    @Nonnull
    public Chunk getOrLoadChunk(BlockPos pos) {
        return getOrLoadChunk(pos.getX() >> Chunk.CHUNK_BLOCK_POS_BIT, pos.getY() >> Chunk.CHUNK_BLOCK_POS_BIT, pos.getZ() >> Chunk.CHUNK_BLOCK_POS_BIT);
    }

    @Nonnull
    public Chunk getOrLoadChunk(int chunkX, int chunkY, int chunkZ) {
        long chunkIndex = getChunkIndex(chunkX, chunkY, chunkZ);
        Chunk chunk = chunks.get(chunkIndex);
        if (chunk == null) {
            chunk = new ChunkImpl(world, chunkX, chunkY, chunkZ);
            chunks.put(chunkIndex, chunk);
            world.getGame().getContext().post(new ChunkLoadEvent(chunk));
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

    private static final int maxPositiveChunkPos = (1 << 20) - 1;

    protected long getChunkIndex(int chunkX, int chunkY, int chunkZ) {
        return abs(chunkX, maxPositiveChunkPos) << 42 | abs(chunkY, maxPositiveChunkPos) << 21 | abs(chunkZ, maxPositiveChunkPos);
    }

    private static int abs(int value, int maxPositiveValue) {
        return value >= 0 ? value : maxPositiveValue - value;
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
