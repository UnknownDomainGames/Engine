package nullengine.world.chunk;

import nullengine.block.Block;
import nullengine.registry.Registries;
import nullengine.util.NibbleArray;

import static nullengine.world.chunk.ChunkConstants.*;

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
        return Registries.getBlockRegistry().getValue(data.getAndSet(getPosIndex(x, y, z), block.getId()));
    }

    private int getPosIndex(int x, int y, int z) {
        return (x & CHUNK_MAX_X) | ((y & CHUNK_MAX_Y) << CHUNK_X_BITS) | ((z & CHUNK_MAX_Z) << CHUNK_X_BITS + CHUNK_Y_BITS);
    }

    public NibbleArray getData() {
        return data;
    }
}
