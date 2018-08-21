package unknowndomain.engine.mod;

import unknowndomain.engine.event.EventBus;
import unknowndomain.engine.mod.java.JavaModLoader;

import java.nio.file.Paths;
import java.util.List;

public class SimpleModManager implements ModManager {

    //TODO: receive home directory of the game and direct to mods folder
    private JavaModLoader javaModLoader;

    public SimpleModManager(EventBus bus) {
        this.javaModLoader = new JavaModLoader(bus, Paths.get("mods"));
    }

    @Override
    public ModContainer getMod(String modId) {
        ModContainer container = javaModLoader.getModContainer(modId);
        if (container != null) return container;
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

    @Override
    public ModContainer whichMod(Class<?> clazz) {
        return null;
    }

}
