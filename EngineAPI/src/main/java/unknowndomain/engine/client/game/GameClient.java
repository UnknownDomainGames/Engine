package unknowndomain.engine.client.game;

import unknowndomain.engine.client.input.controller.EntityController;
import unknowndomain.engine.game.Game;
import unknowndomain.engine.player.Player;
import unknowndomain.engine.world.World;

public interface GameClient extends Game {

    Player getPlayer();

    World getWorld();

    EntityController getEntityController();

    void setEntityController(EntityController controller);

    void clientTick();
}
