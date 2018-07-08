package unknowndomain.engine.api.event.registry;

import unknowndomain.engine.api.event.Event;
import unknowndomain.engine.api.registry.Registry;
import unknowndomain.engine.api.registry.RegistryEntry;
import unknowndomain.engine.api.registry.ResourcesManager;

public class RegistryEvent<T extends RegistryEntry<T>> implements Event {

    private final Registry<T> registry;

    public RegistryEvent(Registry<T> registry){
        this.registry = registry;
    }
}
