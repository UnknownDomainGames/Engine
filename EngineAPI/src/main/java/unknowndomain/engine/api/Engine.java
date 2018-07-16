package unknowndomain.engine.api;

import unknowndomain.engine.api.mod.ModManager;
import unknowndomain.engine.api.resource.ResourcePackManager;

public interface Engine {

	ModManager getModManager();
	
    ResourcePackManager getResourcePackManager();
}
