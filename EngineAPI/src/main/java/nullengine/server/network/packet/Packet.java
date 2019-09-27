package nullengine.server.network.packet;

import nullengine.server.network.PacketBuf;

import java.io.IOException;

public interface Packet {
    void write(PacketBuf buf) throws IOException;

    void read(PacketBuf buf) throws IOException;
}
