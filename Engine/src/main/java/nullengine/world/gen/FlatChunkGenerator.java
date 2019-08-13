package nullengine.world.gen;

import nullengine.block.Block;
import nullengine.event.block.cause.BlockChangeCause;
import nullengine.math.BlockPos;
import nullengine.world.chunk.Chunk;
import nullengine.world.chunk.ChunkConstants;

public class FlatChunkGenerator implements ChunkGenerator {

    private final Block[] layers;

    public FlatChunkGenerator(Block[] layers) {
        this.layers = layers;
    }

    @Override
    public void generate(Chunk chunk) {
        int cx = chunk.getChunkX();
        int cy = chunk.getChunkY();
        int cz = chunk.getChunkZ();
        if (cy < 0) //not making negative-Y chunks
            return;
        for (int j = 0; j < ChunkConstants.SIZE_Y; j++) {
            if (j + cy * ChunkConstants.SIZE_Y >= layers.length) {
                break;
            }
            for (int i = 0; i < ChunkConstants.SIZE_X; i++) {
                for (int k = 0; k < ChunkConstants.SIZE_Z; k++) {
                    chunk.setBlock(BlockPos.of(i, j, k), layers[j + cy * ChunkConstants.SIZE_Y], new BlockChangeCause.WorldGenCause());
                }
            }
        }
    }
}
