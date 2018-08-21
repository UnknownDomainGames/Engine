package unknowndomain.engine.world;

import unknowndomain.engine.event.Event;
import unknowndomain.engine.math.ChunkPos;

public class ChunkLoadEvent implements Event {
    public final ChunkPos pos;
    public final int[][] blocks;

    public ChunkLoadEvent(ChunkPos pos, int[][] blocks) {
        this.pos = pos;
        this.blocks = blocks;
    }
}
