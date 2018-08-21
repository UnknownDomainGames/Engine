package unknowndomain.engine.world;

import unknowndomain.engine.event.Event;
import unknowndomain.engine.math.BlockPos;

public class BlockChangeEvent implements Event {
    public final BlockPos pos;
    public final int blockId;

    public BlockChangeEvent(BlockPos pos, int blockId) {
        this.pos = pos;
        this.blockId = blockId;
    }
}
