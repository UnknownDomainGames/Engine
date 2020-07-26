package engine.server.network.packet;

import engine.server.network.PacketBuf;

import java.io.IOException;

public class PacketUnloadChunk implements Packet {
    private String name;
    private int x;
    private int y;
    private int z;

    public PacketUnloadChunk(String name, int x, int y, int z) {

        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public PacketUnloadChunk() {
    }

    @Override
    public void write(PacketBuf buf) throws IOException {
        buf.writeString(name);
        buf.writeVarInt(x);
        buf.writeVarInt(y);
        buf.writeVarInt(z);
    }

    @Override
    public void read(PacketBuf buf) throws IOException {
        name = buf.readString();
        x = buf.readVarInt();
        y = buf.readVarInt();
        z = buf.readVarInt();
    }

    public String getName() {
        return name;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }
}
