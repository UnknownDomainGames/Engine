package unknowndomain.engine.world;

import unknowndomain.engine.api.block.Block;
import unknowndomain.engine.api.math.BlockPos;
import unknowndomain.engine.api.world.Chunk;

import java.util.HashMap;
import java.util.Map;

public class SimpleChunk implements Chunk {


    private Map<BlockPos, Block> blockMap;

    public SimpleChunk(){
        blockMap = new HashMap<>();
    }

    @Override
    public Block getBlock(BlockPos pos) {
        return blockMap.get(pos);
    }

    @Override
    public void setBlock(BlockPos pos, Block destBlock) {
        blockMap.put(pos, destBlock);
    }
}
