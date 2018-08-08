package unknowndomain.engine.registry;

import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import java.util.Map;

public class SimpleRegistryManager implements RegistryManager {

    private Map<Class<?>, Registry<?>> registries;

    public SimpleRegistryManager(Map<Class<?>, Registry<?>> registries) {
        this.registries = registries;
    }

    @Override
    public <T extends RegistryEntry<T>> Registry<T> getRegistry(@Nonnull Class<T> type) {
        @SuppressWarnings("unchecked")
        Registry<T> registry = (Registry<T>) registries.get(Validate.notNull(type));
        if (registry == null) {
            registry = new SimpleRegistry<T>() {
            };
            registries.put(type, registry);
        }
        return registry;
    }

    @Override
    public <T extends RegistryEntry<T>> boolean hasRegistry(Class<T> type) {
        return registries.containsKey(type);
    }

    @Override
    public <T extends RegistryEntry<T>> void register(T obj) {
        getRegistry(obj.getRegistryType()).register(obj);
    }

}
