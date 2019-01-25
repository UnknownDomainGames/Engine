package unknowndomain.engine._mod;

import java.util.Collection;

public interface ModManager {

    ModContainer getMod(String modId);

    ModContainer getMod(Class<?> clazz);

    boolean isModLoaded(String modId);

    Collection<ModContainer> getLoadedMods();
}
