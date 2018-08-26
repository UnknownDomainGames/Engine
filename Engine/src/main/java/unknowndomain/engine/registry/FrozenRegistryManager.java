package unknowndomain.engine.registry;

import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

public class FrozenRegistryManager implements RegistryManager {
    private final Map<Class<?>, ImmutableRegistry<?>> registries;

    public FrozenRegistryManager(Map<Class<?>, ImmutableRegistry<?>> registries) {
        this.registries = registries;
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
    @SuppressWarnings("unchecked")
    public Collection<Entry<Class<?>, Registry<?>>> getEntries() {
        return (Collection<Entry<Class<?>, Registry<?>>>) (Object) registries.entrySet();
    }
}