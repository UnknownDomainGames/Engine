package unknowndomain.engine.mod;

import unknowndomain.engine.mod.java.JavaModLoader;
import unknowndomain.engine.mod.source.DirModSource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class SimpleModManager implements ModManager {

    public static final Path LOCAL_MODS_DIR = Paths.get("mods");

    private final Map<String, ModContainer> loadedMods = new HashMap<>();

    private final ModLoader javaModLoader = new JavaModLoader();
    private final ModSource localModSource = new DirModSource(LOCAL_MODS_DIR);

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

    public void load() {
        List<LoadableMod> loadableMods = new LinkedList<>();
        loadableMods.addAll(localModSource.getLoadableMods());

        for (LoadableMod loadableMod : loadableMods) {
            ModContainer container = javaModLoader.load(loadableMod);
            loadedMods.put(container.getModId(), container);
        }
    }

    private void sortLoadableMods(List<LoadableMod> loadableMods) {
        // TODO:
        Collections.sort(loadableMods, new Comparator<LoadableMod>() {
            @Override
            public int compare(LoadableMod o1, LoadableMod o2) {
                return 0;
            }
        });
    }

    @Override
    public ModContainer whichMod(Class<?> clazz) {
        // TODO 自动生成的方法存根
        return null;
    }

}
