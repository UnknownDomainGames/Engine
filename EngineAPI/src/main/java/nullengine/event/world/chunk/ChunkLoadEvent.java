package nullengine.event.world.chunk;

import nullengine.event.Event;
import nullengine.world.chunk.Chunk;

public class ChunkLoadEvent implements Event {

    private final Chunk chunk;

    public ChunkLoadEvent(Chunk chunk) {
        this.chunk = chunk;
    }

    public Chunk getChunk() {
        return chunk;
    }
}
