package engine.server.network.packet;

import engine.server.network.PacketBuf;
import engine.world.chunk.Chunk;

import java.io.IOException;

public class PacketChunkData implements Packet {

    private int chunkX;
    private int chunkY;
    private int chunkZ;

    public PacketChunkData(){}

    public PacketChunkData(Chunk chunk){
        this.chunkX = chunk.getX();
        this.chunkY = chunk.getY();
        this.chunkZ = chunk.getZ();

    }

    @Override
    public void write(PacketBuf buf) throws IOException {

    }

    @Override
    public void read(PacketBuf buf) throws IOException {

    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkY() {
        return chunkY;
    }

    public int getChunkZ() {
        return chunkZ;
    }
}
