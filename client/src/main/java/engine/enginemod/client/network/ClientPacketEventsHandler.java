package engine.enginemod.client.network;


import engine.event.Listener;
import engine.server.event.NetworkingStartEvent;
import engine.server.event.PacketReceivedEvent;
import engine.server.network.packet.PacketAlive;

public class ClientPacketEventsHandler {
    @Listener
    public static void onNetworkStart(NetworkingStartEvent event) {
        var bus = event.getNetworkingEventBus();
        bus.register(ChunkPacketsHandler.class);
        bus.register(PlayerRelatedPacketHandler.class);
        bus.register(PreGamePacketHandler.class);
        bus.<PacketReceivedEvent<PacketAlive>, PacketAlive>addGenericListener(PacketAlive.class, e1 -> {
            if (!e1.getPacket().isPong()) {
                e1.getHandler().sendPacket(new PacketAlive(true));
            }
        });
    }


}
