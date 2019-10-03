package nullengine.client.player;

import nullengine.client.input.controller.EntityController;
import nullengine.player.Player;

public interface ClientPlayer extends Player {

    EntityController getEntityController();

    void setEntityController(EntityController controller);
}
