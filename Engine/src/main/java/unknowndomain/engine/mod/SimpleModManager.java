package unknowndomain.engine.mod;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.Validate;
import org.checkerframework.checker.nullness.qual.NonNull;
import unknowndomain.engine.Engine;
import unknowndomain.engine.event.AsmEventBus;
import unknowndomain.engine.event.EventBus;
import unknowndomain.engine.event.registry.RegisterEvent;
import unknowndomain.engine.registry.*;

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

    /**
     * This is really dirty, to make sure that the registry manager actually switch in mod loading context.
     * <p>We have direct method to create a temp event bus to handle this thing</p>
     * <p>This design is not very pretty. Maybe redo later.</p>
     */
    public static RegistryManager register(Collection<ModContainer> containers, Registry.Type... tps) {
        EventBus eventBus = new AsmEventBus();
        Map<Class<?>, Registry<?>> maps = Maps.newHashMap();
        List<MutableRegistry<?>> registries = Lists.newArrayList();
        for (Registry.Type<?> tp : tps) {
            MutableRegistry<?> registry = new MutableRegistry<>(tp.type, tp.name);
            maps.put(tp.type, registry);
            registries.add(registry);
        }
        MutableRegistryManager manager = new MutableRegistryManager(maps);

        for (ModContainer mod : containers) {
            eventBus.register(mod.getInstance()); // register the mod

            for (MutableRegistry<?> registry : registries) {
                registry.setActiveMod(mod);
            }
            eventBus.post(new RegisterEvent(manager));

            eventBus.unregister(mod.getInstance()); // unregister the mod
        }
        ImmutableMap.Builder<Class<?>, Registry<?>> builder = ImmutableMap.builder();
        for (Map.Entry<Class<?>, Registry<?>> entry : manager.getEntries())
            builder.put(entry.getKey(), ImmutableRegistry.freeze(entry.getValue()));
        return manager;
    }

    public static ModManager load(ModStore store, ModRepository modRepository, List<ModMetadata> mods) {
        ImmutableMap.Builder<String, ModContainer> idToMapBuilder = ImmutableMap.builder();
        ImmutableMap.Builder<Class, ModContainer> typeToMapBuilder = ImmutableMap.builder();
        // TODO: sort here
        for (ModMetadata mod : mods) {
            try {
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
                typeToMapBuilder.put(load.getInstance().getClass(), load);
            } catch (Exception e) {
                Engine.getLogger().warn("Fain to load mod " + mod.getId());
                e.printStackTrace();
            }
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
