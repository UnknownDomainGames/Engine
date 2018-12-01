package unknowndomain.engine.event.world.chunk;

import unknowndomain.engine.event.Event;
import unknowndomain.engine.math.ChunkPos;
import unknowndomain.engine.world.World;
import unknowndomain.engine.world.chunk.Chunk;

public class ChunkLoadEvent implements Event {

    private final World world;
    private final ChunkPos pos;
    private final Chunk chunk;

    public ChunkLoadEvent(World world, ChunkPos pos, Chunk chunk) {
        this.world = world;
        this.pos = pos;
        this.chunk = chunk;
    }

    public World getWorld() {
        return world;
    }

    public ChunkPos getPos() {
        return pos;
    }

    public Chunk getChunk() {
        return chunk;
    }
}
