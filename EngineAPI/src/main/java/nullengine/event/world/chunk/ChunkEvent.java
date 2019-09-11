package nullengine.event.world.chunk;

import nullengine.world.chunk.Chunk;

public abstract class ChunkEvent {

    private final Chunk chunk;

    public ChunkEvent(Chunk chunk) {
        this.chunk = chunk;
    }

    public Chunk getChunk() {
        return chunk;
    }
}
