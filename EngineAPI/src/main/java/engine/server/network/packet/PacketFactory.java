package engine.server.network.packet;

@FunctionalInterface
public interface PacketFactory<T extends Packet> {
    T create();
}
