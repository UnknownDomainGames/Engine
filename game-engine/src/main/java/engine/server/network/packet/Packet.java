package engine.server.network.packet;

import engine.server.network.PacketBuf;

import java.io.IOException;

public interface Packet {
    void write(PacketBuf buf) throws IOException;

    void read(PacketBuf buf) throws IOException;
}
