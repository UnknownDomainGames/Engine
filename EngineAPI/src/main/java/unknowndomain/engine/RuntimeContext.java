package unknowndomain.engine;

import unknowndomain.engine.block.BlockObject;
import unknowndomain.engine.registry.IdentifiedRegistry;

public class RuntimeContext {
    private IdentifiedRegistry<BlockObject> blockRegistry;

    public RuntimeContext(IdentifiedRegistry<BlockObject> blockRegistry) {
        this.blockRegistry = blockRegistry;
    }

    public IdentifiedRegistry<BlockObject> getBlockRegistry() {
        return blockRegistry;
    }

    public void send(Object o) {

    }
}
