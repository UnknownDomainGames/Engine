package engine.event.world.chunk;

import engine.world.chunk.Chunk;

public final class ChunkLoadEvent extends ChunkEvent {
    public ChunkLoadEvent(Chunk chunk) {
        super(chunk);
    }
}
