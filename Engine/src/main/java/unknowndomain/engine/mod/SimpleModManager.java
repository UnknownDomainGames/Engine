package unknowndomain.engine.mod;

import org.apache.commons.lang3.Validate;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collection;
import java.util.Map;

public class SimpleModManager implements ModManager {
    private Map<String, ModContainer> idToMods;
    private Map<Class, ModContainer> typeToMods;

    public SimpleModManager(Map<String, ModContainer> idToMods, Map<Class, ModContainer> typeToMods) {
        this.idToMods = idToMods;
        this.typeToMods = typeToMods;
    }

    @Override
    public ModContainer findMod(String modId) {
        return idToMods.get(Validate.notNull(modId));
    }

    @Override
    public ModContainer findMod(Class<?> clazz) {
        return typeToMods.get(Validate.notNull(clazz));
    }

    @Override
    public boolean isModLoaded(String modId) {
        return idToMods.containsKey(Validate.notNull(modId));
    }

//    public void load() {
//        List<LoadableMod> loadableMods = new LinkedList<>();
//        loadableMods.addAll(localModSource.getLoadableMods());
//
//        sortLoadableMods(loadableMods);
//
//        for (LoadableMod loadableMod : loadableMods) {
//            try {
//                ModContainer container = javaModLoader.load(loadableMod);
//                loadedMods.put(container.getModId(), container);
//            } catch (ModLoadException e) {
//                Platform.getLogger().warn(e.getMessage(), e);
//            }
//        }
//    }

//    private void sortLoadableMods(List<LoadableMod> loadableMods) {
//        Collections.sort(loadableMods, (mod1, mod2) -> {
//                    final String modId1 = mod1.getMetadata().getModId(), modId2 = mod2.getMetadata().getModId();
//                    for (ModDependencyEntry entry : mod1.getMetadata().getDependencies()) {
//                        if (!entry.getModId().equals(modId2))
//                            continue;
//
//                        switch (entry.getLoadOrder()) {
//                            case AFTER:
//                            case REQUIRED:
//                                return 1; // After o2 load
//                            case BEFORE:
//                                return -1; // Before o2 load
//                        }
//                    }
//
//                    for (ModDependencyEntry entry : mod2.getMetadata().getDependencies()) {
//                        if (!entry.getModId().equals(modId1))
//                            continue;
//
//                        switch (entry.getLoadOrder()) {
//                            case AFTER:
//                            case REQUIRED:
//                                return -1; // After o1 load
//                            case BEFORE:
//                                return 1; // Before o1 load
//                        }
//                    }
//
//                    return 0;
//                }
//        );
//    }

    @NonNull
    @Override
    public Collection<ModContainer> getLoadedMods() {
        return idToMods.values();
    }
}
