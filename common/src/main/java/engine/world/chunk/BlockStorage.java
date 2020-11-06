package engine.world.chunk;

import engine.block.state.BlockState;
import engine.registry.Registries;
import engine.util.NibbleArray;

import static engine.world.chunk.ChunkConstants.*;

public class BlockStorage {

    private final NibbleArray data;

    public BlockStorage() {
        this.data = new NibbleArray(8, BLOCK_COUNT);
    }

    public BlockState getBlock(int x, int y, int z) {
        return Registries.getBlockRegistry().getStateFromId(data.get(getPosIndex(x, y, z)));
    }

    public BlockState setBlock(int x, int y, int z, BlockState block) {
        return Registries.getBlockRegistry().getStateFromId(data.getAndSet(getPosIndex(x, y, z), Registries.getBlockRegistry().getStateId(block)));
    }

    private int getPosIndex(int x, int y, int z) {
        return (x & CHUNK_MAX_X) | ((y & CHUNK_MAX_Y) << CHUNK_X_BITS) | ((z & CHUNK_MAX_Z) << CHUNK_X_BITS + CHUNK_Y_BITS);
    }

    public NibbleArray getData() {
        return data;
    }
}
