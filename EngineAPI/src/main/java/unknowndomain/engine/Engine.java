package unknowndomain.engine;

import unknowndomain.engine.game.Game;
import unknowndomain.engine.mod.ModManager;
import unknowndomain.engine.client.resource.ResourceManager;

public interface Engine {
	
    ModManager getModManager();
    
    Game getGame();

    ResourceManager getResourcePackManager();
}
