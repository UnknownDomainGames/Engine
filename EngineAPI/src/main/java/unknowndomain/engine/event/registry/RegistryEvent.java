package unknowndomain.engine.event.registry;

import unknowndomain.engine.event.Event;
import unknowndomain.engine.registry.Registry;
import unknowndomain.engine.registry.RegistryEntry;

public class RegistryEvent<T extends RegistryEntry<T>> implements Event {
    private final Registry<T> registry;

    public RegistryEvent(Registry<T> registry){
        this.registry = registry;
    }

    public Registry<T> getRegistry() {
        return registry;
    }
}
