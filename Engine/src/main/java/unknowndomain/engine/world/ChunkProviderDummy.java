package unknowndomain.engine.world;

import unknowndomain.engine.GameContext;
import unknowndomain.engine.block.BlockObject;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.math.ChunkPos;
import unknowndomain.engine.registry.IdentifiedRegistry;

public class ChunkProviderDummy implements ChunkProvider {
    @Override
    public Chunk provideChunk(GameContext context, BlockPos pos) {
        LogicChunk chunk = new LogicChunk(context);

        IdentifiedRegistry<BlockObject> reg = context.getBlockRegistry();
        BlockObject value = reg.getValue(1);
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                chunk.setBlock(new BlockPos(i, 0, j), value);
            }
        }

        ChunkPos chunkPos = pos.toChunk();
        context.post(new LogicWorld.ChunkLoad(chunkPos, chunk.data));
        return chunk;
    }
}
