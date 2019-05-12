package unknowndomain.game.init;

import unknowndomain.engine.block.AirBlock;
import unknowndomain.engine.block.BaseBlock;
import unknowndomain.engine.block.Block;

public class Blocks {

    public static Block AIR = new AirBlock();
    public static Block GRASS = new BaseBlock().registerName("grass");
    public static Block DIRT = new BaseBlock().registerName("dirt");
}
