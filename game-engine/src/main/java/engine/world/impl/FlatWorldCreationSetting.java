package engine.world.impl;

import engine.block.state.BlockState;
import engine.world.WorldCreationSetting;

public class FlatWorldCreationSetting implements WorldCreationSetting {

    public static FlatWorldCreationSetting create() {
        return new FlatWorldCreationSetting();
    }

    private BlockState[] layers;

    public BlockState[] getLayers() {
        return this.layers;
    }

    public FlatWorldCreationSetting layers(BlockState[] layers) {
        this.layers = layers;
        return this;
    }
}
