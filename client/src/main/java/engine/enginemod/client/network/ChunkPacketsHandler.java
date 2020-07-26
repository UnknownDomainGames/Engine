package engine.enginemod.client.network;

import engine.Platform;
import engine.client.world.WorldClient;
import engine.event.Listener;
import engine.server.event.PacketReceivedEvent;
import engine.server.network.packet.PacketChunkData;
import engine.server.network.packet.PacketUnloadChunk;

public class ChunkPacketsHandler {

    @Listener
    public static void onChunkDataReceived(PacketReceivedEvent<PacketChunkData> event) {
        if (Platform.getEngineClient().isPlaying()) {
            Platform.getEngineClient().getCurrentGame().getWorld(event.getPacket().getWorldName())
                    .ifPresent(world -> ((WorldClient) world).getChunkManager().loadChunkFromPacket(event.getPacket()));
        }
    }

    @Listener
    public static void onReceiveChunkUnloadNotice(PacketReceivedEvent<PacketUnloadChunk> event) {
        if (Platform.getEngine().isPlaying()) {
            Platform.getEngine().getCurrentGame().getWorld(event.getPacket().getName())
                    .ifPresent(world -> ((WorldClient) world).getChunkManager().getChunk(event.getPacket().getX(), event.getPacket().getY(), event.getPacket().getZ())
                            .ifPresent(chunk -> ((WorldClient) world).getChunkManager().unloadChunk(chunk)));
        }
    }
}
