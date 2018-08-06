package unknowndomain.engine;

import java.util.function.Consumer;

import unknowndomain.engine.block.BlockObject;
import unknowndomain.engine.registry.IdentifiedRegistry;

public class RuntimeContext {
    private IdentifiedRegistry<BlockObject> blockRegistry;
    private Consumer<Object> bus; // replace to event bus

    public RuntimeContext(IdentifiedRegistry<BlockObject> blockRegistry, Consumer<Object> bus) {
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
