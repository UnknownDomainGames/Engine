package engine.world.impl;

import engine.block.Block;
import engine.world.WorldCreationSetting;

public class FlatWorldCreationSetting implements WorldCreationSetting {

    public static FlatWorldCreationSetting create() {
        return new FlatWorldCreationSetting();
    }

    private Block[] layers;

    public Block[] getLayers() {
        return this.layers;
    }

    public FlatWorldCreationSetting layers(Block[] layers) {
        this.layers = layers;
        return this;
    }
}
