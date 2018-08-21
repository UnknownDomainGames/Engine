package unknowndomain.engine.world;

import io.netty.util.collection.LongObjectHashMap;
import io.netty.util.collection.LongObjectMap;
import org.checkerframework.checker.nullness.qual.NonNull;
import unknowndomain.engine.GameContext;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.math.ChunkPos;

import javax.annotation.Nonnull;
import java.util.Collection;

public class ChunkStore implements Chunk.Store {
    // should do the io operation to load chunk
    private LongObjectMap<Chunk> chunks = new LongObjectHashMap<>();
    private GameContext gameContext;

    public ChunkStore(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    @Override
    public Collection<Chunk> getChunks() {
        return chunks.values();
    }

    @NonNull
    @Override
    public Chunk getChunk(@NonNull BlockPos pos) {
        ChunkPos chunkPos = pos.toChunk();
        long cp = (long) chunkPos.getChunkX() << 32 | chunkPos.getChunkZ();
        Chunk chunk = this.chunks.get(cp);
        if (chunk != null)
            return chunk;
        Chunk0 c = new Chunk0(gameContext);
        c.data = decorateChunk();
        this.chunks.put(cp, c);
        gameContext.post(new ChunkLoadEvent(pos.toChunk(), c.data));
        return c;
    }

    @Override
    public void touchChunk(@Nonnull BlockPos pos) {
        ChunkPos chunkPos = pos.toChunk();
        long cp = (long) chunkPos.getChunkX() << 32 | chunkPos.getChunkZ();
        Chunk chunk = this.chunks.get(cp);
        if (chunk != null) {
            Chunk0 c = new Chunk0(gameContext);
            c.data = decorateChunk();
            this.chunks.put(cp, c);
            gameContext.post(new ChunkLoadEvent(pos.toChunk(), c.data));
        }
    }

    @Override
    public void discardChunk(@Nonnull BlockPos pos) {
        ChunkPos chunkPos = pos.toChunk();
        long cp = (long) chunkPos.getChunkX() << 32 | chunkPos.getChunkZ();
        Chunk remove = this.chunks.remove(cp);
        // save this chunk?
    }

    private int[][] decorateChunk() {
        int[][] data = new int[16][16 * 16 * 16];
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                int x = i;
                int y = 0;
                int z = j;
                data[y / 16][x << 8 | y << 4 | z] = 2;
            }
        }
        return data;
    }
}
