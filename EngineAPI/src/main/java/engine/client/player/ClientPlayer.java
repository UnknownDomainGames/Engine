package engine.client.player;

import engine.client.input.controller.EntityController;
import engine.player.Player;

public interface ClientPlayer extends Player {

    EntityController getEntityController();

    void setEntityController(EntityController controller);
}
