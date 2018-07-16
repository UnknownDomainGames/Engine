package unknowndomain.engine.api.mod;

import java.util.List;

public interface ModManager {
	
	ModContainer getMod(String modId);
	
	boolean isModLoaded(String modId);

	List<ModContainer> getAllLoadedMods();

}
