package unknowndomain.engine.event.registry;

import unknowndomain.engine.event.Event;
import unknowndomain.engine.registry.RegistryManager;

public class RegistrationStartEvent implements Event {
    private final RegistryManager manager;

    public RegistrationStartEvent(RegistryManager registryManager) {
        manager = registryManager;
    }

    public RegistryManager getRegistryManager() {
        return manager;
    }
}
