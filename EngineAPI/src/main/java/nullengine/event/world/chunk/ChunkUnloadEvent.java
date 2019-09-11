package nullengine.event.world.chunk;

import nullengine.world.chunk.Chunk;

public final class ChunkUnloadEvent extends ChunkEvent {
    public ChunkUnloadEvent(Chunk chunk) {
        super(chunk);
    }
}
