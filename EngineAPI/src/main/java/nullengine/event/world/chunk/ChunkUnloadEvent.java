package nullengine.event.world.chunk;

import nullengine.event.Event;
import nullengine.world.chunk.Chunk;

public class ChunkUnloadEvent implements Event {
    private Chunk chunk;

    public ChunkUnloadEvent(Chunk chunk) {
        this.chunk = chunk;
    }

    public Chunk getChunk() {
        return chunk;
    }
}
