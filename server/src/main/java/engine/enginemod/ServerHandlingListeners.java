package engine.enginemod;

import engine.Platform;
import engine.entity.component.TwoHands;
import engine.event.Listener;
import engine.game.GameServerFullAsync;
import engine.registry.Registries;
import engine.server.event.PacketReceivedEvent;
import engine.server.network.ConnectionStatus;
import engine.server.network.ServerGameplayNetworkHandlerContext;
import engine.server.network.ServerLoginNetworkHandlerContext;
import engine.server.network.packet.PacketHandshake;
import engine.server.network.packet.PacketProvider;
import engine.server.network.packet.PacketSyncRegistry;
import engine.server.network.packet.c2s.PacketLoginProfile;
import engine.server.network.packet.c2s.PacketTwoHandComponentChange;
import engine.server.network.packet.s2c.PacketLoginRequest;
import engine.server.network.packet.s2c.PacketLoginSuccess;

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
                event.getHandler().sendPacket(new PacketSyncRegistry(Registries.getBlockRegistry()));
                event.getHandler().sendPacket(new PacketSyncRegistry(Registries.getItemRegistry()));
                event.getHandler().sendPacket(new PacketSyncRegistry(Registries.getRegistryManager().getRegistry(PacketProvider.class).get()));
                event.getHandler().setStatus(ConnectionStatus.GAMEPLAY, new ServerGameplayNetworkHandlerContext(player));
                playerManager.onPlayerConnect(event.getHandler(), player);
            } else {
                //TODO: login
            }
        }
    }

    @Listener
    public static void onTwoHandComponentChanged(PacketReceivedEvent<PacketTwoHandComponentChange> event) {
        if (event.getHandler().getStatus() == ConnectionStatus.GAMEPLAY) {
            ((ServerGameplayNetworkHandlerContext) event.getHandler().getContext()).getPlayer().getControlledEntity().getComponent(TwoHands.class)
                    .ifPresent(twoHands -> {
                        if (event.getPacket().isMainHandChanged()) {
                            twoHands.setMainHand(event.getPacket().getMainHand());
                        }
                        if (event.getPacket().isOffHandChanged()) {
                            twoHands.setOffHand(event.getPacket().getOffHand());
                        }
                    });
        }
    }
}
