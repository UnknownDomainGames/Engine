package unknowndomain.engine.api;

import unknowndomain.engine.api.game.Game;
import unknowndomain.engine.api.mod.ModManager;
import unknowndomain.engine.api.resource.ResourceManager;

public interface Engine {
	
    ModManager getModManager();
    
    Game getGame();

    ResourceManager getResourcePackManager();
}
