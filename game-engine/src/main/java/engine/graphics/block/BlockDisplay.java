package engine.graphics.block;

import engine.block.state.BlockState;
import engine.client.asset.AssetURL;
import engine.component.Component;

import java.util.HashMap;
import java.util.Map;

public class BlockDisplay implements Component {

    private AssetURL modelUrl;
    private Map<BlockState, AssetURL> variantModelUrls = new HashMap<>();
    private boolean visible = true;

    public AssetURL getModelUrl() {
        return modelUrl;
    }

    public Map<BlockState, AssetURL> getVariantModelUrls() {
        return variantModelUrls;
    }

    public BlockDisplay model(String url) {
        modelUrl = AssetURL.fromString(url);
        return this;
    }

    public BlockDisplay variant(BlockState state, String url) {
        variantModelUrls.put(state, AssetURL.fromString(url));
        return this;
    }

    public boolean isVisible() {
        return visible;
    }

    public BlockDisplay visible(boolean visible) {
        this.visible = visible;
        return this;
    }
}
