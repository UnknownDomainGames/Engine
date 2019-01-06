package unknowndomain.engine.world.chunk;

import unknowndomain.engine.block.Block;
import unknowndomain.engine.util.BitArray;

public class BlockStorage {

    private final Chunk chunk;
    private final BitArray data;

    public BlockStorage(Chunk chunk) {
        this.chunk = chunk;
        this.data = new BitArray(16, 4096);
    }

    public Block getBlock(int x, int y, int z) {
        return chunk.getWorld().getGame().getContext().getBlockRegistry().getValue(getRawData(x, y, z));
    }

    public int getRawData(int x, int y, int z) {
        return data.get(getPosIndex(x, y, z));
    }

    public Block setBlock(int x, int y, int z, Block block) {
        return chunk.getWorld().getGame().getContext().getBlockRegistry().getValue(data.getAndSet(getPosIndex(x, y, z), chunk.getWorld().getGame().getContext().getBlockRegistry().getId(block)));
    }

    private int getPosIndex(int x, int y, int z) {
        return ((x & 0xF) << 8) | ((y & 0xF) << 4) | z & 0xF;
    }
}
