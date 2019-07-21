package nullengine.world.impl;

import nullengine.block.Block;
import nullengine.world.WorldCreationSetting;

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
