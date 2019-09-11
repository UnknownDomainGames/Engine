package nullengine.event.world.chunk;

import nullengine.world.chunk.Chunk;

public final class ChunkLoadEvent extends ChunkEvent {
    public ChunkLoadEvent(Chunk chunk) {
        super(chunk);
    }
}
