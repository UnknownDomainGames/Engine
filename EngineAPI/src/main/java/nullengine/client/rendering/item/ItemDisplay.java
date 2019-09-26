package nullengine.client.rendering.item;

import nullengine.client.asset.AssetURL;
import nullengine.component.Component;

public class ItemDisplay implements Component {

    private AssetURL modelUrl;

    public AssetURL getModelUrl() {
        return modelUrl;
    }

    public ItemDisplay model(AssetURL modelUrl) {
        this.modelUrl = modelUrl;
        return this;
    }

    public ItemDisplay model(String modelUrl) {
        this.modelUrl = AssetURL.fromString(modelUrl);
        return this;
    }
}
