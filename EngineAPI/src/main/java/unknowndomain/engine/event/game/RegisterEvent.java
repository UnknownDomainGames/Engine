package unknowndomain.engine.event.game;

import unknowndomain.engine.event.Event;
import unknowndomain.engine.registry.RegistryEntry;
import unknowndomain.engine.registry.RegistryManager;

public class RegisterEvent implements Event {

    private final RegistryManager registryManager;

    public RegisterEvent(RegistryManager registryManager) {
        this.registryManager = registryManager;
    }

    public RegistryManager getRegistryManager() {
        return registryManager;
    }

    public <T extends RegistryEntry<T>> T register(T obj) {
        return registryManager.register(obj);
    }

    public <T extends RegistryEntry<T>> void registerAll(T... objs) {
        registryManager.registerAll(objs);
    }
}
