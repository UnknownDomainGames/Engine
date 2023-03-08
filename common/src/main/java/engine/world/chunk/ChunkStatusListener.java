package engine.world.chunk;

import org.joml.Vector3ic;

public interface ChunkStatusListener {

    void updateGraphCenter(Vector3ic pos);

    void onChunkStatusUpdate(Vector3ic pos, ChunkStatus status);

    void stop();
}
