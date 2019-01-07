package unknowndomain.engine.client;

import unknowndomain.engine.client.resource.ResourceManager;
import unknowndomain.engine.game.Game;

public interface GameClient extends Game {

    /**
     * @return the resource manager
     */
    ResourceManager getResourceManager();
}
