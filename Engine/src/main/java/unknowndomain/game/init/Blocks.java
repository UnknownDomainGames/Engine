package unknowndomain.game.init;

import unknowndomain.engine.block.Block;
import unknowndomain.engine.block.BlockAir;
import unknowndomain.engine.block.BlockBase;

public class Blocks {

    public static Block AIR = new BlockAir();
    public static Block GRASS = new BlockBase().registerName("grass");
    public static Block DIRT = new BlockBase().registerName("dirt");
}
