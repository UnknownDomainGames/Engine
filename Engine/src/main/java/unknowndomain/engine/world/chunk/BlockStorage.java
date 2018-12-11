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
//        if (y < 0) {
//            return context.getBlockRegistry().getValue(0);
//        }
        return getBlock((short) (((x & 0xF) << 8) | ((y & 0xF) << 4) | z & 0xF));
    }

    private Block getBlock(short posIndex) {
        return chunk.getWorld().getGame().getContext().getBlockRegistry().getValue(data.get(posIndex));
    }

    public Block setBlock(int x, int y, int z, Block destBlock) {
//        if (y < 0) {
//            return context.getBlockRegistry().getValue(0);
//        }
        return setBlock((short) (((x & 0xF) << 8) | ((y & 0xF) << 4) | z & 0xF), destBlock);
    }

    private Block setBlock(short posIndex, Block destBlock) {
        return chunk.getWorld().getGame().getContext().getBlockRegistry().getValue(data.getAndSet(posIndex, chunk.getWorld().getGame().getContext().getBlockRegistry().getId(destBlock)));
    }
}
