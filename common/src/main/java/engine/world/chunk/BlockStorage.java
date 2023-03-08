package engine.world.chunk;

import engine.block.state.BlockState;
import engine.registry.Registries;
import engine.util.NibbleArray;

public class BlockStorage {

    private final NibbleArray data;

    public BlockStorage() {
        this.data = new NibbleArray(8, Chunk.BLOCK_COUNT);
    }

    public BlockState getBlock(int x, int y, int z) {
        return Registries.getBlockRegistry().getStateFromId(data.get(getPosIndex(x, y, z)));
    }

    public BlockState setBlock(int x, int y, int z, BlockState block) {
        return Registries.getBlockRegistry().getStateFromId(data.getAndSet(getPosIndex(x, y, z), Registries.getBlockRegistry().getStateId(block)));
    }

    private int getPosIndex(int x, int y, int z) {
        return ((y & Chunk.CHUNK_MAX_Y) << Chunk.CHUNK_X_BITS + Chunk.CHUNK_Z_BITS) | ((z & Chunk.CHUNK_MAX_Z) << Chunk.CHUNK_X_BITS) | (x & Chunk.CHUNK_MAX_X);
    }

    public NibbleArray getData() {
        return data;
    }
}
