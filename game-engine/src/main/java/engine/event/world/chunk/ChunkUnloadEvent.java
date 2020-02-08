package engine.event.world.chunk;

import engine.world.chunk.Chunk;

public final class ChunkUnloadEvent extends ChunkEvent {
    public ChunkUnloadEvent(Chunk chunk) {
        super(chunk);
    }
}
