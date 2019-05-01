package unknowndomain.engine.event.registry;

import unknowndomain.engine.event.GenericEvent;
import unknowndomain.engine.registry.Registry;
import unknowndomain.engine.registry.RegistryEntry;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;

public class RegisterEvent<T extends RegistryEntry<T>> implements GenericEvent<T> {

    private final Registry<T> registry;

    public RegisterEvent(Registry<T> registry) {
        this.registry = registry;
    }

    @Override
    public Type getGenericType() {
        return registry.getEntryType();
    }

    public T register(@Nonnull T obj) {
        return registry.register(obj);
    }

    public void registerAll(@Nonnull T... objs) {
        registry.registerAll(objs);
    }
}
