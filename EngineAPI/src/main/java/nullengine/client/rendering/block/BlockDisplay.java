package nullengine.client.rendering.block;

import nullengine.client.asset.AssetURL;
import nullengine.component.Component;

public class BlockDisplay implements Component {

    private AssetURL modelUrl;
    private boolean visible = true;

    public AssetURL getModelUrl() {
        return modelUrl;
    }

    public BlockDisplay model(String url) {
        modelUrl = AssetURL.fromString(url);
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
