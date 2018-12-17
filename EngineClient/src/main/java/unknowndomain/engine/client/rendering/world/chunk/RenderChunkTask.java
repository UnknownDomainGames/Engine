package unknowndomain.engine.client.rendering.world.chunk;

import unknowndomain.engine.math.ChunkPos;
import unknowndomain.engine.util.BlockPosIterator;
import unknowndomain.engine.world.World;
import unknowndomain.engine.world.chunk.Chunk;

public class RenderChunkTask {

    private static final ThreadLocal<BlockPosIterator> blockPosIterator = ThreadLocal
            .withInitial(() -> new BlockPosIterator(0, 0, 0, 15, 15, 15));

    private World world;
    private ChunkPos pos;
    private Chunk chunk;


}
