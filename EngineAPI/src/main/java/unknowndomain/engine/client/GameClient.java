package unknowndomain.engine.client;

import unknowndomain.engine.game.Game;
import unknowndomain.engine.player.Player;
import unknowndomain.engine.world.World;

public interface GameClient extends Game {

    Player getPlayer();

    World getWorld();

    ClientContext getClientContext();
}
