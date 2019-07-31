package nullengine.server.event;

import nullengine.event.Event;
import nullengine.server.network.packet.Packet;

public class PacketReceivedEvent<T extends Packet> implements Event {
    private final T packet;

    public PacketReceivedEvent(T packet){
        this.packet = packet;
    }

    public T getPacket() {
        return packet;
    }
}
