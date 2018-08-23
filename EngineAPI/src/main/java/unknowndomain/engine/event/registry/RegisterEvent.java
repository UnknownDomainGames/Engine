package unknowndomain.engine.event.registry;

import unknowndomain.engine.event.Event;
import unknowndomain.engine.registry.RegistryManager;

public class RegisterEvent implements Event {
    private RegistryManager.Mutable registry;

    public RegisterEvent(RegistryManager.Mutable registry) {
        this.registry = registry;
    }

    public RegistryManager.Mutable getRegistry() {
        return registry;
    }
}
