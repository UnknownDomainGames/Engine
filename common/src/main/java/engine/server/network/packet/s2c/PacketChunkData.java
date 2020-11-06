package engine.server.network.packet.s2c;

import engine.server.network.PacketBuf;
import engine.server.network.packet.Packet;
import engine.world.chunk.CubicChunk;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketChunkData implements Packet {

    private String worldName;
    private int chunkX;
    private int chunkY;
    private int chunkZ;
    private byte[] rawData;

    public PacketChunkData() {
    }

    public PacketChunkData(CubicChunk chunk) {
        this.worldName = chunk.getWorld().getName();
        this.chunkX = chunk.getX();
        this.chunkY = chunk.getY();
        this.chunkZ = chunk.getZ();
        try (var income = new ByteArrayOutputStream()) {
            chunk.writeBlockContent(new DataOutputStream(income));
            rawData = income.toByteArray();
        } catch (IOException e) {

        }
    }

    @Override
    public void write(PacketBuf buf) throws IOException {
        buf.writeString(worldName);
        buf.writeVarInt(chunkX);
        buf.writeVarInt(chunkY);
        buf.writeVarInt(chunkZ);
        buf.writeBytes(rawData);
    }

    @Override
    public void read(PacketBuf buf) throws IOException {
        worldName = buf.readString();
        chunkX = buf.readVarInt();
        chunkY = buf.readVarInt();
        chunkZ = buf.readVarInt();
        try (var income = new ByteArrayOutputStream()) {
            buf.readBytes(income, buf.readableBytes());
            rawData = income.toByteArray();
        }
    }

    public String getWorldName() {
        return worldName;
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

    public byte[] getRawData() {
        return rawData;
    }
}
