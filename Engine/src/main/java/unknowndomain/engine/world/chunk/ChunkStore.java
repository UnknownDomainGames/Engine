package unknowndomain.engine.world.chunk;

import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.collection.LongObjectMap;
import org.checkerframework.checker.nullness.qual.NonNull;
import unknowndomain.engine.game.GameContext;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.math.ChunkPos;
import unknowndomain.engine.world.World;

import javax.annotation.Nonnull;
import java.util.Collection;

public class ChunkStore implements Chunk.Store {
    // should do the io operation to load chunk
    private final World world;

    private LongObjectMap<Chunk> chunks = new LongObjectHashMap<>();

    public ChunkStore(World world) {
        this.world = world;
    }

    @Override
    public Collection<Chunk> getChunks() {
        return chunks.values();
    }

    @NonNull
    @Override
    public Chunk getChunk(@NonNull BlockPos pos) {
        long cp = getChunkPos(pos);
        Chunk chunk = this.chunks.get(cp);
        if (chunk != null)
            return chunk;
        ChunkImpl c = new ChunkImpl(world);
//        c.data = decorateChunk();
        this.chunks.put(cp, c);
        return c;
    }

    @Override
    public void touchChunk(@Nonnull BlockPos pos) {
        long cp = getChunkPos(pos);
        Chunk chunk = this.chunks.get(cp);
        if (chunk != null) {
            ChunkImpl c = new ChunkImpl(world);
//            c.data = decorateChunk();
            this.chunks.put(cp, c);
        }
    }

    @Override
    public void discardChunk(@Nonnull BlockPos pos) {
        long cp = getChunkPos(pos);
        Chunk remove = this.chunks.remove(cp);
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

    private long getChunkPos(BlockPos blockPos) {
        ChunkPos chunkPos = blockPos.toChunkPos();
        return (long) chunkPos.getChunkX() << 42 | blockPos.getY() << 21 | chunkPos.getChunkZ();
    }
}
