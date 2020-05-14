package engine.enginemod.client.network;

import engine.Platform;
import engine.client.world.WorldClient;
import engine.event.Listener;
import engine.server.event.PacketReceivedEvent;
import engine.server.network.packet.PacketChunkData;

public class ChunkPacketsHandler {

    @Listener
    public static void onChunkDataReceived(PacketReceivedEvent<PacketChunkData> event) {
        if (Platform.getEngineClient().isPlaying()) {
            Platform.getEngineClient().getClientGame().getWorld(event.getPacket().getWorldName())
                    .ifPresent(world -> ((WorldClient) world).getChunkManager().loadChunkFromPacket(event.getPacket()));
        }
    }
}
