package unknowndomain.engine.registry.impl;

import org.checkerframework.checker.nullness.qual.NonNull;
import unknowndomain.engine.registry.Registry;
import unknowndomain.engine.registry.RegistryEntry;
import unknowndomain.engine.registry.RegistryManager;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import static java.util.Objects.requireNonNull;

public class SimpleRegistryManager implements RegistryManager {

    private final Map<Class<?>, Registry<?>> registries;

    public SimpleRegistryManager(Map<Class<?>, Registry<?>> registries) {
        this.registries = registries;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends RegistryEntry<T>> Registry<T> getRegistry(@Nonnull Class<T> type) {
        requireNonNull(type);
        if (!hasRegistry(type)) {
            throw new UnsupportedOperationException(String.format("Has not registered registry for type %s", type.getSimpleName()));
        }
        return (Registry<T>) registries.get(type);
    }

    @Override
    public <T extends RegistryEntry<T>> boolean hasRegistry(@NonNull Class<T> type) {
        return registries.containsKey(type);
    }

    @Override
    public <T extends RegistryEntry<T>> void register(@NonNull T obj) {
        getRegistry(obj.getEntryType()).register(obj);
    }

    @Override
    public Collection<Entry<Class<?>, Registry<?>>> getEntries() {
        return registries.entrySet();
    }
}
