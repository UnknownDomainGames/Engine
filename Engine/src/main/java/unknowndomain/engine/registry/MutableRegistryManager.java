package unknowndomain.engine.registry;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.Validate;
import org.checkerframework.checker.nullness.qual.NonNull;

class MutableRegistryManager implements RegistryManager.Mutable {
    Map<Class<?>, Registry<?>> registries;

    MutableRegistryManager(Map<Class<?>, Registry<?>> registries) {
        this.registries = registries;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends RegistryEntry<T>> Registry<T> getRegistry(@Nonnull Class<T> type) {
        return (Registry<T>) registries.computeIfAbsent(Validate.notNull(type), c -> new MutableRegistry(c));
    }

    @Override
    public <T extends RegistryEntry<T>> boolean hasRegistry(@NonNull Class<T> type) {
        return registries.containsKey(type);
    }

    @Override
    public <T extends RegistryEntry<T>> void register(@NonNull T obj) {
        getRegistry(obj.getRegistryType()).register(obj);
    }

    @Override
    public Collection<Entry<Class<?>, Registry<?>>> getEntries() {
        return registries.entrySet();
    }
}
