package nullengine.event.world.chunk;

import nullengine.event.Event;
import nullengine.world.chunk.Chunk;

public abstract class ChunkEvent implements Event {

    private final Chunk chunk;

    public ChunkEvent(Chunk chunk) {
        this.chunk = chunk;
    }

    public Chunk getChunk() {
        return chunk;
    }
}
