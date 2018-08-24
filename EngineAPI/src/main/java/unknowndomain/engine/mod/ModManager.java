package unknowndomain.engine.mod;

import java.util.Collection;

public interface ModManager {

    ModContainer getLoadedMod(String modId);

    boolean isModLoaded(String modId);

    Collection<ModContainer> getLoadedMods();

    ModContainer whichMod(Class<?> clazz);
}
