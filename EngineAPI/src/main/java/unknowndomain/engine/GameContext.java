package unknowndomain.engine;

import unknowndomain.engine.block.Block;
import unknowndomain.engine.event.Event;
import unknowndomain.engine.event.EventBus;
import unknowndomain.engine.item.Item;
import unknowndomain.engine.registry.IdentifiedRegistry;
import unknowndomain.engine.registry.RegistryManager;

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

    public IdentifiedRegistry<Block> getBlockRegistry() {
        return (IdentifiedRegistry<Block>) manager.getRegistry(Block.class);
    }

    public IdentifiedRegistry<Item> getItemRegistry() {
        return (IdentifiedRegistry<Item>) manager.getRegistry(Item.class);
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
