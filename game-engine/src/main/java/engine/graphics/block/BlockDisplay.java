package engine.graphics.block;

import engine.block.state.BlockState;
import engine.client.asset.AssetURL;
import engine.component.Component;
import engine.graphics.queue.RenderType;

import java.util.HashMap;
import java.util.Map;

public class BlockDisplay implements Component {

    private AssetURL modelUrl;
    private Map<BlockState, AssetURL> variantModelUrls = new HashMap<>();
    private RenderType renderType = RenderType.OPAQUE;
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

    public RenderType getRenderType() {
        return renderType;
    }

    public BlockDisplay renderType(RenderType type) {
        if (type == RenderType.OPAQUE || type == RenderType.TRANSPARENT || type == RenderType.TRANSLUCENT) {
            this.renderType = type;
        }
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
