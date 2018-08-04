package unknowndomain.engine.mod;

import java.io.File;
import java.util.List;

public class SimpleModManager implements ModManager {

	//TODO: receive home directory of the game and direct to mods folder
	private JavaModLoader javaModLoader = new JavaModLoader(new File("/mods/").toPath());

	@Override
	public ModContainer getMod(String modId) {
	    ModContainer container = javaModLoader.getModContainer(modId);
	    if(container != null) return container;
	    //TODO: there might be more mod loader
		return null;
	}

	@Override
	public boolean isModLoaded(String modId) {
	    //TODO: there might be more mod loader
		return javaModLoader.getLoadedMods().containsKey(modId);
	}

	@Override
	public List<ModContainer> getAllLoadedMods() {
	    //TODO: there might be more mod loader
		return javaModLoader.getLoadedModsList();
	}

}
