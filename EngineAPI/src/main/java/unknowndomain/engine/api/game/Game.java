package unknowndomain.engine.api.game;

import unknowndomain.engine.api.mod.ModManager;
import unknowndomain.engine.api.resource.ResourceManager;
import unknowndomain.engine.api.resource.ResourcePackManager;

public interface Game {
    ModManager getModManager();
    ResourcePackManager getResourcePackManager();
}
