package unknowndomain.engine.registry.impl;

import org.checkerframework.checker.nullness.qual.NonNull;
import unknowndomain.engine.registry.Registry;
import unknowndomain.engine.registry.RegistryEntry;
import unknowndomain.engine.registry.RegistryManager;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

public class SimpleRegistryManager implements RegistryManager {

    private final Map<Class<?>, Registry<?>> registries;

    public SimpleRegistryManager(Map<Class<?>, Registry<?>> registries) {
        this.registries = registries;
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends RegistryEntry<T>> Registry<T> getRegistry(Class<T> type) {
        Registry<T> registry = (Registry<T>) registries.get(type);
        if (registry == null) {
            throw new UnsupportedOperationException(String.format("Has not registered registry for type %s", type.getSimpleName()));
        }
        return registry;
    }

    @Override
    public <T extends RegistryEntry<T>> boolean hasRegistry(Class<T> type) {
        return registries.containsKey(type);
    }

    @Override
    public <T extends RegistryEntry<T>> T register(@NonNull T obj) {
        return getRegistry(obj.getEntryType()).register(obj);
    }

    @Nonnull
    @Override
    public Collection<Entry<Class<?>, Registry<?>>> getEntries() {
        return registries.entrySet();
    }
}
