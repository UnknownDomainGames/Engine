package unknowndomaingame.foundation.init;

import nullengine.block.AirBlock;
import nullengine.block.BaseBlock;
import nullengine.block.Block;

public class Blocks {

    public static Block AIR = new AirBlock();
    public static Block GRASS = new BaseBlock().registerName("grass");
    public static Block DIRT = new BaseBlock().registerName("dirt");
}
