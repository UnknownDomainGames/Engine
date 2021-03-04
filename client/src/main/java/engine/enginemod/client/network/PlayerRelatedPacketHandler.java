package engine.enginemod.client.network;

import engine.Platform;
import engine.client.game.GameClient;
import engine.event.Listener;
import engine.server.event.PacketReceivedEvent;
import engine.server.network.packet.s2c.PacketPlayerPosView;

public class PlayerRelatedPacketHandler {

    @Listener
    public static void onPlayerPosView(PacketReceivedEvent<PacketPlayerPosView> event) {
        if (Platform.getEngine().isPlaying()) {
            var game = Platform.getEngine().getCurrentClientGame();
            if (game instanceof GameClient) {
                var player = game.getClientPlayer();
                if (player.getControlledEntity() != null) {
                    player.handlePosViewSync(event);
                }
            }
        }
    }
}
