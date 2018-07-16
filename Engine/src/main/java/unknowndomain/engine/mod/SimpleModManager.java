package unknowndomain.engine.mod;

import unknowndomain.engine.api.mod.ModContainer;
import unknowndomain.engine.api.mod.ModManager;

import java.util.List;

public class SimpleModManager implements ModManager {

	@Override
	public ModContainer getMod(String modId) {
		return null;
	}

	@Override
	public boolean isModLoaded(String modId) {
		return false;
	}

	@Override
	public List<ModContainer> getAllLoadedMods() {
		return null;
	}

}
