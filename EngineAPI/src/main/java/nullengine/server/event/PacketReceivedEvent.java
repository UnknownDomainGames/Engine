package nullengine.server.event;

import nullengine.event.Event;
import nullengine.event.GenericEvent;
import nullengine.server.network.NetworkHandler;
import nullengine.server.network.packet.Packet;

import java.lang.reflect.Type;

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
