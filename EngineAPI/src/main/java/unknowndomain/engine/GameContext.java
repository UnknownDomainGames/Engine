package unknowndomain.engine;

import unknowndomain.engine.block.BlockObject;
import unknowndomain.engine.registry.IdentifiedRegistry;
import unknowndomain.engine.registry.RegistryManager;

import java.util.function.Consumer;

public class GameContext {
    private RegistryManager manager;
    private IdentifiedRegistry<BlockObject> blockRegistry;
    private Consumer<Object> bus; // replace to event bus

    public GameContext(IdentifiedRegistry<BlockObject> blockRegistry, Consumer<Object> bus) {
        this.blockRegistry = blockRegistry;
        this.bus = bus;
    }

    public IdentifiedRegistry<BlockObject> getBlockRegistry() {
        return blockRegistry;
    }

    public void send(Object o) {
        bus.accept(o);
    }
}
