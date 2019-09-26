package nullengine.world.chunk;

import nullengine.block.Block;
import nullengine.registry.Registries;
import nullengine.server.network.PacketBuf;
import nullengine.util.NibbleArray;

public class BlockStorage {

    private final Chunk chunk;
    private final NibbleArray data;

    public BlockStorage(Chunk chunk) {
        this.chunk = chunk;
        this.data = new NibbleArray(16, 4096);
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
        return ((x & 0xF) << 8) | ((y & 0xF) << 4) | z & 0xF;
    }

    public void writeToPacketBuffer(PacketBuf buf){

    }

    public void readFromPacketBuffer(PacketBuf buf){

    }
}
