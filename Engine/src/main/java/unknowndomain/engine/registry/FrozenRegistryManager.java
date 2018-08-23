package unknowndomain.engine.registry;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import org.apache.commons.lang3.Validate;

import unknowndomain.engine.event.EventBus;
import unknowndomain.engine.event.registry.RegisterEvent;

public class FrozenRegistryManager implements RegistryManager {
    private final Map<Class<?>, Registry<?>> registries;

    private FrozenRegistryManager(Map<Class<?>, Registry<?>> registries) {
        this.registries = registries;
    }

    public static RegistryManager collectAll(EventBus bus, Registry.Type<? extends RegistryEntry<?>>... tps) {
        Map<Class<?>, Registry<?>> maps = Maps.newHashMap();
        for (Registry.Type<?> tp : tps) {
            maps.put(tp.type, new MutableRegistry<>(tp.type, tp.name));
        }
        MutableRegistryManager manager = new MutableRegistryManager(maps);
        bus.post(new RegisterEvent(manager));

        ImmutableMap.Builder<Class<?>, Registry<?>> builder = ImmutableMap.builder();
        for (Entry<Class<?>, Registry<?>> entry : manager.registries.entrySet())
            builder.put(entry.getKey(), ImmutableRegistry.freeze(entry.getValue()));
        return new FrozenRegistryManager(builder.build());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends RegistryEntry<T>> Registry<T> getRegistry(@Nonnull Class<T> type) {
        return (Registry<T>) registries.get(Validate.notNull(type));
    }

    @Override
    public <T extends RegistryEntry<T>> boolean hasRegistry(@Nonnull Class<T> type) {
        return registries.containsKey(type);
    }

    @Override
    public Collection<Entry<Class<?>, Registry<?>>> getEntries() {
        return registries.entrySet();
    }
}