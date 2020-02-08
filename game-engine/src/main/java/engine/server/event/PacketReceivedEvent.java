package engine.server.event;

import engine.event.GenericEvent;
import engine.server.network.NetworkHandler;
import engine.server.network.packet.Packet;

public class PacketReceivedEvent<T extends Packet> extends GenericEvent.Impl<T> {
    private final T packet;
    private final NetworkHandler handler;

    public PacketReceivedEvent(NetworkHandler handler, T packet){
        super(packet.getClass());
        this.handler = handler;
        this.packet = packet;
    }

    public NetworkHandler getHandler() {
        return handler;
    }

    public T getPacket() {
        return packet;
    }
}
