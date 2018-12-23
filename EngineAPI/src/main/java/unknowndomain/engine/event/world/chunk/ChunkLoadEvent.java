package unknowndomain.engine.event.world.chunk;

import unknowndomain.engine.event.Event;
import unknowndomain.engine.world.chunk.Chunk;

public class ChunkLoadEvent implements Event {

    private final Chunk chunk;

    public ChunkLoadEvent(Chunk chunk) {
        this.chunk = chunk;
    }

    public Chunk getChunk() {
        return chunk;
    }
}
