package unknowndomain.engine.event.world.chunk;

import unknowndomain.engine.event.Event;
import unknowndomain.engine.world.chunk.Chunk;

public class ChunkUnloadEvent implements Event {
    private Chunk chunk;

    public ChunkUnloadEvent(Chunk chunk) {
        this.chunk = chunk;
    }

    public Chunk getChunk() {
        return chunk;
    }
}
