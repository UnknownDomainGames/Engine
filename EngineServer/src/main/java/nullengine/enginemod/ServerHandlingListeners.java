package nullengine.enginemod;

import nullengine.event.Listener;
import nullengine.mod.annotation.AutoListen;
import nullengine.server.event.PacketReceivedEvent;
import nullengine.server.network.ConnectionStatus;
import nullengine.server.network.packet.PacketHandshake;

public class ServerHandlingListeners {

    @Listener
    public void onClientHandshake(PacketReceivedEvent<PacketHandshake> event){
        if (event.getHandler().getStatus() == ConnectionStatus.HANDSHAKE) {
            if(event.getPacket().getWantedStatus() == ConnectionStatus.LOGIN){
                if(!event.getHandler().isLocal()){
                    //TODO check client version and mods
                }
                event.getHandler().setStatus(ConnectionStatus.LOGIN);
            }
        }
    }
}
