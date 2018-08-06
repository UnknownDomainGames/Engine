package unknowndomain.engine.mod;

import java.util.List;

public interface ModManager {

    ModContainer getMod(String modId);

    boolean isModLoaded(String modId);

    List<ModContainer> getAllLoadedMods();

    ModContainer whichMod(Class<?> clazz);
}
