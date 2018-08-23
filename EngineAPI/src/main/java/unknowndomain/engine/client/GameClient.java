package unknowndomain.engine.client;

import unknowndomain.engine.action.ActionManager;
import unknowndomain.engine.client.resource.ResourceManager;
import unknowndomain.engine.game.Game;

public interface GameClient extends Game {

    /**
     * @return the resource manager
     */
    ResourceManager getResourceManager();

    /**
     * I'm considering if we need this live in engine or game
     */
    ActionManager getActionManager();
}
