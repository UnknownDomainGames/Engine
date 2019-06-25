package nullengine.client.event.game;

import nullengine.event.Event;
import nullengine.registry.RegistryEntry;
import nullengine.registry.RegistryManager;

public class ClientRegisterEvent implements Event {

    private final RegistryManager registryManager;

    public ClientRegisterEvent(RegistryManager registryManager) {
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
