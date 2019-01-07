package unknowndomain.engine.event.registry;

import unknowndomain.engine.event.Event;
import unknowndomain.engine.registry.RegistryManager;

/**
 * 
 * @Deprecated Replaced by EngineEvent.RegistrationStart
 */
@Deprecated
public class RegisterEvent implements Event {
    private RegistryManager registry;

    public RegisterEvent(RegistryManager registry) {
        this.registry = registry;
    }

    public RegistryManager getRegistry() {
        return registry;
    }
}
