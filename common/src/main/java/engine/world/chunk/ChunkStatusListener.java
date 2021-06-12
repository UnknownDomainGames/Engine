package engine.world.chunk;

public interface ChunkStatusListener {

    void updateGraphCenter(ChunkPos pos);

    void onChunkStatusUpdate(ChunkPos pos, ChunkStatus status);

    void stop();
}
