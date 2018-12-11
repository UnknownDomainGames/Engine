package unknowndomain.engine.world.chunk;

import unknowndomain.engine.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.Collection;

@Deprecated
public interface ChunkStorage {
    Collection<Chunk> getChunks();

    @Nonnull
    Chunk getChunk(@Nonnull BlockPos pos);

    /**
     * Touch the chunk at the the position, ensure it loaded
     */
    void touchChunk(@Nonnull BlockPos chunkPos);

    /**
     * Dispose the chunk at the position
     */
    void discardChunk(@Nonnull BlockPos chunkPos);
}
