package unknowndomain.engine;

import unknowndomain.engine.block.BlockObject;
import unknowndomain.engine.registry.IdentifiedRegistry;
import unknowndomain.engine.registry.RegistryManager;
import unknowndomain.engine.event.Event;
import unknowndomain.engine.event.EventBus;

public class GameContext implements EventBus {
    private RegistryManager manager;
    private EventBus bus;

    public GameContext(RegistryManager manager, EventBus bus) {
        this.manager = manager;
        this.bus = bus;
    }
    
    public RegistryManager getManager() {
        return manager;
    }

    public IdentifiedRegistry<BlockObject> getBlockRegistry() {
        return (IdentifiedRegistry<BlockObject>) manager.getRegistry(BlockObject.class);
    }

	@Override
	public boolean post(Event event) {
		return bus.post(event);
	}

	@Override
	public void register(Object listener) {
		bus.register(listener);
	}

	@Override
	public void unregister(Object listener) {
		bus.unregister(listener);
	}
}
