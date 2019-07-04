package nullengine.registry.impl;

import nullengine.registry.Registry;
import nullengine.registry.RegistryEntry;
import nullengine.registry.RegistryException;
import nullengine.registry.RegistryManager;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

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

    @Override
    public <T extends RegistryEntry<T>> void addRegistry(Class<T> type, Supplier<Registry<T>> supplier) {
        if (registries.containsKey(type)) {
            throw new RegistryException("Registry has been registered");
        }
        registries.put(type, supplier.get());
    }

    @Nonnull
    @Override
    public Collection<Entry<Class<?>, Registry<?>>> getEntries() {
        return registries.entrySet();
    }
}
