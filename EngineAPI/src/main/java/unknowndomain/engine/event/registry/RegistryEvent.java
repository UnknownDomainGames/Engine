package unknowndomain.engine.event.registry;

import unknowndomain.engine.registry.Registry;
import unknowndomain.engine.registry.RegistryEntry;

public class RegistryEvent<T extends RegistryEntry<T>> {

    private final Registry<T> registry;

    public RegistryEvent(Registry<T> registry){
        this.registry = registry;
    }
}
