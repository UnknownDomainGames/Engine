package unknowndomain.engine.world;

import org.checkerframework.checker.nullness.qual.NonNull;
import unknowndomain.engine.GameContext;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.math.ChunkPos;

import java.util.*;

public class ChunkProviderDummy implements Chunk.Provider {

    private Map<ChunkPos, Chunk> chunks;

    public ChunkProviderDummy(){
        chunks = new HashMap<>();
    }

    @Override
    public Collection<Chunk> getChunks() {
        return chunks.values();
    }

    @NonNull
    @Override
    public Chunk getChunk(@NonNull GameContext context, @NonNull BlockPos pos) {
        ChunkPos chunkPos = pos.toChunk();
        if(chunks.containsKey(chunkPos)){
            return chunks.get(chunkPos);
        }

        LogicChunk chunk = new LogicChunk(context);
        chunks.put(chunkPos,chunk);
        System.out.println("CREATE CHUNK");

        int[][] data = new int[16][16 * 16 * 16];
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                int x = i;
                int y = 0;
                int z = j;
                data[y / 16][x << 8 | y << 4 | z] = 1;
            }
        }
        chunk.data = data;
        context.post(new LogicWorld.ChunkLoad(chunkPos, chunk.data));
        return chunk;
    }
}
