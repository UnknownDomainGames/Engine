package engine.event.world.chunk;

import engine.event.Event;
import engine.world.chunk.Chunk;

public abstract class ChunkEvent implements Event {

    private final Chunk chunk;

    public ChunkEvent(Chunk chunk) {
        this.chunk = chunk;
    }

    public Chunk getChunk() {
        return chunk;
    }
}
