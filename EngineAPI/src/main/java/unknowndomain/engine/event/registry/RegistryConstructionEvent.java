package unknowndomain.engine.event.registry;

import unknowndomain.engine.event.Event;
import unknowndomain.engine.registry.Registry;

import java.util.Map;

public class RegistryConstructionEvent implements Event {
    private final Map<Class<?>, Registry<?>> registries;

    public RegistryConstructionEvent(Map<Class<?>, Registry<?>> registries) {
        this.registries = registries;
    }

    public void register(Registry<?> registry) {
        if (registries.containsKey(registry.getEntryType())) {
            throw new IllegalStateException(String.format("Registry<%s> has been registered.", registry.getEntryType().getSimpleName()));
        }
        registries.put(registry.getEntryType(), registry);
    }
}
