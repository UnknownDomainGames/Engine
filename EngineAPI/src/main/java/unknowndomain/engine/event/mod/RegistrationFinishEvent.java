package unknowndomain.engine.event.mod;

import unknowndomain.engine.event.Event;
import unknowndomain.engine.registry.RegistryManager;

public class RegistrationFinishEvent implements Event {
    private final RegistryManager manager;

    public RegistrationFinishEvent(RegistryManager registryManager) {
        manager = registryManager;
    }

    public RegistryManager getRegistryManager() {
        return manager;
    }
}
