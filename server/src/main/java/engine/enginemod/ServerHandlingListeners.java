package engine.enginemod;

import engine.Platform;
import engine.event.Listener;
import engine.game.GameServerFullAsync;
import engine.server.event.PacketReceivedEvent;
import engine.server.network.ConnectionStatus;
import engine.server.network.ServerGameplayNetworkHandlerContext;
import engine.server.network.ServerLoginNetworkHandlerContext;
import engine.server.network.packet.PacketHandshake;
import engine.server.network.packet.PacketLoginProfile;
import engine.server.network.packet.PacketLoginRequest;
import engine.server.network.packet.PacketLoginSuccess;

public class ServerHandlingListeners {

    @Listener
    public static void onClientHandshake(PacketReceivedEvent<PacketHandshake> event){
        if (event.getHandler().getStatus() == ConnectionStatus.HANDSHAKE) {
            if (event.getPacket().getWantedStatus() == ConnectionStatus.LOGIN) {
                if (!event.getHandler().isLocal()) {
                    //TODO check client version and mods
                }
                event.getHandler().setStatus(ConnectionStatus.LOGIN, new ServerLoginNetworkHandlerContext());
                event.getHandler().sendPacket(new PacketLoginRequest());
//                event.getHandler().sendPacket(new PacketDisconnect("Test server connection"));
//                event.getHandler().closeChannel();
            }
//            if (event.getPacket().getWantedStatus() == ConnectionStatus.GAMEPLAY) { //TODO: Handshake status should not go to Gameplay directly
//                event.getHandler().setStatus(ConnectionStatus.GAMEPLAY, new ServerGameplayNetworkHandlerContext());
//                var playerManager = ((GameServerFullAsync) Platform.getEngine().getCurrentGame()).getPlayerManager();
//                playerManager.onPlayerConnect(event.getHandler(), playerManager.createPlayer(event.getHandler(), new Profile(UUID.randomUUID(), "Player")));
////                event.getHandler().closeChannel();
//            }
        }
    }

    @Listener
    public static void onLoginProfileReceived(PacketReceivedEvent<PacketLoginProfile> event) {
        if (event.getHandler().getStatus() == ConnectionStatus.LOGIN) {
            ((ServerLoginNetworkHandlerContext) event.getHandler().getContext()).setProfile(event.getPacket().getProfile());
            if (event.getHandler().isLocal() || true /* TODO: check if server shall request Online mode */) {
                event.getHandler().sendPacket(new PacketLoginSuccess(((ServerLoginNetworkHandlerContext) event.getHandler().getContext()).getProfile()));
                //TODO: straight to game_prepare
                var playerManager = ((GameServerFullAsync) Platform.getEngine().getCurrentGame()).getPlayerManager();
                var player = playerManager.createPlayer(event.getHandler(), ((ServerLoginNetworkHandlerContext) event.getHandler().getContext()).getProfile());
                event.getHandler().setStatus(ConnectionStatus.GAMEPLAY, new ServerGameplayNetworkHandlerContext(player));
                playerManager.onPlayerConnect(event.getHandler(), player);
            } else {
                //TODO: login
            }
        }
    }
}
