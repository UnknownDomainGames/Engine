package engine.world.gen;

import engine.block.state.BlockState;
import engine.event.block.cause.BlockChangeCause;
import engine.math.BlockPos;
import engine.world.chunk.Chunk;

import static engine.world.chunk.ChunkConstants.*;

public class FlatChunkGenerator implements ChunkGenerator {

    private final BlockState[] layers;

    public FlatChunkGenerator(BlockState[] layers) {
        this.layers = layers;
    }

    @Override
    public void generate(Chunk chunk) {
        int cx = chunk.getX();
        int cy = chunk.getY();
        int cz = chunk.getZ();
        if (cy < 0) //not making negative-Y chunks
            return;
        for (int j = 0; j < CHUNK_Y_SIZE; j++) {
            if (j + cy * CHUNK_Y_SIZE >= layers.length) {
                break;
            }
            for (int i = 0; i < CHUNK_X_SIZE; i++) {
                for (int k = 0; k < CHUNK_Z_SIZE; k++) {
                    chunk.setBlock(BlockPos.of(i, j, k), layers[j + cy * CHUNK_Y_SIZE], new BlockChangeCause.WorldGenCause());
                }
            }
        }
    }
}
