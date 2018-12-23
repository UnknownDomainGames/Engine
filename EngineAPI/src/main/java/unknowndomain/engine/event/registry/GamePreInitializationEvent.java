package unknowndomain.engine.event.registry;

import unknowndomain.engine.block.Block;
import unknowndomain.engine.event.Event;

public class GamePreInitializationEvent implements Event {

    private Block blockAir;

    public Block getBlockAir() {
        return blockAir;
    }

    public void setBlockAir(Block blockAir) {
        this.blockAir = blockAir;
    }
}
