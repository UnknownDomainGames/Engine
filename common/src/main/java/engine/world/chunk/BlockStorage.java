package engine.world.chunk;

import engine.block.Block;
import engine.registry.Registries;
import engine.util.NibbleArray;

import static engine.world.chunk.ChunkConstants.*;

public class BlockStorage {

    private final NibbleArray data;

    public BlockStorage() {
        this.data = new NibbleArray(8, BLOCK_COUNT);
    }

    public Block getBlock(int x, int y, int z) {
        return Registries.getBlockRegistry().getValue(getBlockId(x, y, z));
    }

    public int getBlockId(int x, int y, int z) {
        return data.get(getPosIndex(x, y, z));
    }

    public Block setBlock(int x, int y, int z, Block block) {
        return Registries.getBlockRegistry().getValue(data.getAndSet(getPosIndex(x, y, z), Registries.getBlockRegistry().getId(block)));
    }

    private int getPosIndex(int x, int y, int z) {
        return (x & CHUNK_MAX_X) | ((y & CHUNK_MAX_Y) << CHUNK_X_BITS) | ((z & CHUNK_MAX_Z) << CHUNK_X_BITS + CHUNK_Y_BITS);
    }

    public NibbleArray getData() {
        return data;
    }
}
