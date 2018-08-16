package unknowndomain.engine.world;

import unknowndomain.engine.GameContext;
import unknowndomain.engine.block.BlockObject;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.math.ChunkPos;
import unknowndomain.engine.registry.IdentifiedRegistry;

// this class is just for test
public class ChunkProviderDummy implements ChunkProvider {
    @Override
    public Chunk provideChunk(GameContext context, BlockPos pos) { //what is this 'pos' actually means?
    // the chunk location
        LogicChunk chunk = new LogicChunk(context); // we might want to get rid of interface now
                                                    // this could be chunkpos passing through
        // where is Mouse
        IdentifiedRegistry<BlockObject> reg = context.getBlockRegistry();
        BlockObject value = reg.getValue(1);
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                chunk.setBlock(new BlockPos(i, 0, j), value);
            }
        }
        
        // it's fine to use this, if we are careful for all the cases that this is a different 
        // coord (chunk pos is not block world coord)        
        ChunkPos chunkPos = pos.toChunk(); //this function convert the blockpos to chunkpos by dividing! Shall we make a direct one that copy the value of Blockpos to Chunkpos only?
        context.post(new LogicWorld.ChunkLoad(chunkPos, chunk.data));
        return chunk;
    }
}
