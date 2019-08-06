package nullengine.server.network.packet;

import nullengine.registry.RegistryEntry;
import nullengine.server.network.PacketBuf;

import java.io.IOException;

public interface Packet extends RegistryEntry<Packet> {
    void write(PacketBuf buf) throws IOException;

    void read(PacketBuf buf) throws IOException;
}
