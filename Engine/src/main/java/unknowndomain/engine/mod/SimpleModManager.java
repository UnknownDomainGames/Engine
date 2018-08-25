package unknowndomain.engine.mod;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.Validate;
import org.checkerframework.checker.nullness.qual.NonNull;
import unknowndomain.engine.Engine;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SimpleModManager implements ModManager {
    private Map<String, ModContainer> idToMods;
    private Map<Class, ModContainer> typeToMods;

    private SimpleModManager(Map<String, ModContainer> idToMods, Map<Class, ModContainer> typeToMods) {
        this.idToMods = idToMods;
        this.typeToMods = typeToMods;
    }

    public static ModManager load(ModStore store, ModRepository modRepository, List<ModMetadata> mods) {
        ImmutableMap.Builder<String, ModContainer> idToMapBuilder = ImmutableMap.builder();
        ImmutableMap.Builder<Class, ModContainer> typeToMapBuilder = ImmutableMap.builder();
        // TODO: sort here
        for (ModMetadata mod : mods) {
            if (!store.exists(mod)) {
                if (!modRepository.contains(mod)) {
                    Engine.getLogger().warn("Cannot find mod " + mod + " from local or other sources! Skip to load!");
                    continue;
                }
                store.store(mod, modRepository.open(mod));
            }
            ModContainer load = store.load(mod);
            if (load == null) {
                Engine.getLogger().warn("Some exceptions happened during loading mod {0} from local! Skip to load!", mod);
                continue;
            }
            idToMapBuilder.put(mod.getId(), load);
            typeToMapBuilder.put(load.getSource().getClass(), load);
        }
        return new SimpleModManager(idToMapBuilder.build(), typeToMapBuilder.build());
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

    @NonNull
    @Override
    public Collection<ModContainer> getLoadedMods() {
        return idToMods.values();
    }


}
