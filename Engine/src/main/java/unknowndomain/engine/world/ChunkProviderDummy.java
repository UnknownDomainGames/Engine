package unknowndomain.engine.world;

import org.joml.Vector3i;
import unknowndomain.engine.RuntimeContext;
import unknowndomain.engine.block.BlockObject;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.math.ChunkPos;
import unknowndomain.engine.registry.IdentifiedRegistry;

public class ChunkProviderDummy implements ChunkProvider {
    @Override
    public Chunk provideChunk(RuntimeContext context, BlockPos pos) {
        LogicChunk chunk = new LogicChunk(context);

        IdentifiedRegistry<BlockObject> reg = context.getBlockRegistry();
        BlockObject value = reg.getValue(1);
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                chunk.setBlock(new BlockPos(i, 0, j), value);
            }
        }

        ChunkPos chunkPos = pos.toChunk();
        context.send(new LogicWorld.ChunkLoad(chunkPos, chunk.data));
        return chunk;
    }
}
