package engine.enginemod;

import engine.Platform;
import engine.event.Listener;
import engine.server.event.PacketReceivedEvent;
import engine.server.network.ConnectionStatus;
import engine.server.network.packet.PacketDisconnect;
import engine.server.network.packet.PacketGameData;
import engine.server.network.packet.PacketHandshake;

public class ServerHandlingListeners {

    @Listener
    public static void onClientHandshake(PacketReceivedEvent<PacketHandshake> event){
        if (event.getHandler().getStatus() == ConnectionStatus.HANDSHAKE) {
            if (event.getPacket().getWantedStatus() == ConnectionStatus.LOGIN) {
                if (!event.getHandler().isLocal()) {
                    //TODO check client version and mods
                }
                event.getHandler().setStatus(ConnectionStatus.LOGIN);
                event.getHandler().sendPacket(new PacketDisconnect("Test server connection"));
                event.getHandler().closeChannel();
            }
            if (event.getPacket().getWantedStatus() == ConnectionStatus.GAMEPLAY) { //TODO: Handshake status should not go to Gameplay directly
                event.getHandler().setStatus(ConnectionStatus.GAMEPLAY);
                event.getHandler().sendPacket(new PacketGameData(Platform.getEngine().getCurrentGame().getData()));
//                event.getHandler().closeChannel();
            }
        }
    }
}
