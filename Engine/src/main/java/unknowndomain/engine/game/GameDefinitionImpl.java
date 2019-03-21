package unknowndomain.engine.game;

import unknowndomain.engine.block.Block;

public class GameDefinitionImpl implements GameDefinition {

    private final Block blockAir;

    public GameDefinitionImpl(Block blockAir) {
        this.blockAir = blockAir;
    }

    @Override
    public Block blockAir() {
        return blockAir;
    }
}
