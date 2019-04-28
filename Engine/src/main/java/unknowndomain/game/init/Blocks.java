package unknowndomain.game.init;

import unknowndomain.engine.block.Block;
import unknowndomain.engine.block.BlockBuilder;
import unknowndomain.engine.block.impl.BlockAir;

public class Blocks {

    public static Block AIR = new BlockAir();
    public static Block GRASS = BlockBuilder.create("grass").build();
    public static Block DIRT = BlockBuilder.create("dirt").build();
}
