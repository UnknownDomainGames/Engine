package unknowndomain.engine.mod;

import unknowndomain.engine.mod.java.JavaModLoader;

import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SimpleModManager implements ModManager {

	private final Map<String, ModContainer> loadedMods = new HashMap<>();

	//TODO: receive home directory of the game and direct to mods folder
	private JavaModLoader javaModLoader = new JavaModLoader(Paths.get("mods"));

	@Override
	public ModContainer getLoadedMod(String modId) {
	    return loadedMods.get(modId);
	}

	@Override
	public boolean isModLoaded(String modId) {
		return loadedMods.containsKey(modId);
	}

	@Override
	public Collection<ModContainer> getLoadedMods() {
		return loadedMods.values();
	}

    @Override
    public ModContainer whichMod(Class<?> clazz) {
        // TODO 自动生成的方法存根
        return null;
    }

}
