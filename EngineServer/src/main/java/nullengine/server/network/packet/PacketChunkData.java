package nullengine.server.network.packet;

import nullengine.server.network.PacketBuf;
import nullengine.world.chunk.Chunk;

import java.io.IOException;

public class PacketChunkData extends BasePacket {

    private int chunkX;
    private int chunkY;
    private int chunkZ;

    public PacketChunkData(){}

    public PacketChunkData(Chunk chunk){
        this.chunkX = chunk.getChunkX();
        this.chunkY = chunk.getChunkY();
        this.chunkZ = chunk.getChunkZ();

    }

    @Override
    public void write(PacketBuf buf) throws IOException {

    }

    @Override
    public void read(PacketBuf buf) throws IOException {

    }
}
