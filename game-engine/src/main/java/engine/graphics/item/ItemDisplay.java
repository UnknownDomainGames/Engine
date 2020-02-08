package engine.graphics.item;

import engine.client.asset.AssetURL;
import engine.component.Component;

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
