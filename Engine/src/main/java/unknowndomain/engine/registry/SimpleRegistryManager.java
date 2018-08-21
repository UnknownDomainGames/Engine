package unknowndomain.engine.registry;

import org.apache.commons.lang3.Validate;
import unknowndomain.engine.event.EventBus;
import unknowndomain.engine.event.registry.RegistryEvent;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

public class SimpleRegistryManager implements RegistryManager {
    private Map<Class<?>, Registry<?>> registries;

    public SimpleRegistryManager(Map<Class<?>, Registry<?>> registries) {
        this.registries = registries;
    }

    public static <T extends RegistryEntry<T>> Registry<T> collect(EventBus bus, Registry.Type<T> tp) {
        MutableRegistry<T> registry = new MutableRegistry<>(tp.type, tp.name);
        RegistryEvent<T> event = new RegistryEvent<>(registry);
        bus.post(event);
        return ImmutableRegistry.freeze(event.getRegistry());
    }

    public static RegistryManager collectAll(EventBus bus, Registry.Type<? extends RegistryEntry<?>>... tps) {
        for (Registry.Type<?> tp : tps) {
            collect(bus, tp);
        }
        return null;
    }

    @Override
    public <T extends RegistryEntry<T>> Registry<T> getRegistry(@Nonnull Class<T> type) {
        @SuppressWarnings("unchecked")
        Registry<T> registry = (Registry<T>) registries.get(Validate.notNull(type));
        if (registry == null) {
            throw new Error();
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

    @Override
    public <T extends RegistryEntry<T>> void addRegistry(Class<T> type, Registry<T> registry) {

    }

    @Override
    public Collection<Entry<Class<?>, Registry<?>>> getEntries() {
        return null;
    }

}
