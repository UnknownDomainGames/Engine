package engine.enginemod.client.network;


import engine.event.Listener;
import engine.server.event.NetworkingStartEvent;

public class ClientPacketEventsHandler {
    @Listener
    public static void onNetworkStart(NetworkingStartEvent event) {
        var bus = event.getNetworkingEventBus();
        bus.register(ChunkPacketsHandler.class);
    }
}
