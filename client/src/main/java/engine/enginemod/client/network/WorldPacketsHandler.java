package engine.enginemod.client.network;

import engine.Platform;
import engine.event.Listener;
import engine.event.block.cause.BlockChangeCause;
import engine.server.event.PacketReceivedEvent;
import engine.server.network.packet.s2c.PacketBlockUpdate;

public class WorldPacketsHandler {
    @Listener
    public static void onBlockUpdate(PacketReceivedEvent<PacketBlockUpdate> event) {
        Platform.getEngine().getCurrentClientGame().getWorld(event.getPacket().getWorldName()).ifPresent(world ->
                world.setBlock(event.getPacket().getPos(), event.getPacket().getBlock(), new BlockChangeCause.WorldSyncCause(), false));
    }
}
