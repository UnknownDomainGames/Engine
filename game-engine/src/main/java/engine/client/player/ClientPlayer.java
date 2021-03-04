package engine.client.player;

import engine.client.input.controller.EntityController;
import engine.player.Player;
import engine.server.event.PacketReceivedEvent;

public interface ClientPlayer extends Player {

    void handlePosViewSync(PacketReceivedEvent event);

    EntityController getEntityController();

    void setEntityController(EntityController controller);
}
