package unknowndomain.engine.mod;

import unknowndomain.engine.Platform;
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

        sortLoadableMods(loadableMods);

        for (LoadableMod loadableMod : loadableMods) {
            try {
                ModContainer container = javaModLoader.load(loadableMod);
                loadedMods.put(container.getModId(), container);
            } catch (ModLoadException e) {
                Platform.getLogger().warn(e.getMessage(), e);
            }
        }
    }

    private void sortLoadableMods(List<LoadableMod> loadableMods) {
        Collections.sort(loadableMods, (mod1, mod2) -> {
                    final String modId1 = mod1.getMetadata().getModId(), modId2 = mod2.getMetadata().getModId();
                    for (ModDependencyEntry entry : mod1.getMetadata().getDependencies()) {
                        if (!entry.getModId().equals(modId2))
                            continue;

                        switch (entry.getLoadOrder()) {
                            case AFTER:
                            case REQUIRED:
                                return 1; // After o2 load
                            case BEFORE:
                                return -1; // Before o2 load
                        }
                    }

                    for (ModDependencyEntry entry : mod2.getMetadata().getDependencies()) {
                        if (!entry.getModId().equals(modId1))
                            continue;

                        switch (entry.getLoadOrder()) {
                            case AFTER:
                            case REQUIRED:
                                return -1; // After o1 load
                            case BEFORE:
                                return 1; // Before o1 load
                        }
                    }

                    return 0;
                }
        );
    }

    @Override
    public ModContainer whichMod(Class<?> clazz) {
        // TODO 自动生成的方法存根
        return null;
    }

}
