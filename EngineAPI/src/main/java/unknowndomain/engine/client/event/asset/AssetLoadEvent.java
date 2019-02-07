package unknowndomain.engine.client.event.asset;

import unknowndomain.engine.client.asset.AssetLoadManager;
import unknowndomain.engine.client.asset.source.AssetManager;
import unknowndomain.engine.event.Event;

public class AssetLoadEvent implements Event {

    private final AssetLoadManager assetLoadManager;
    private final AssetManager assetManager;

    public AssetLoadEvent(AssetLoadManager assetLoadManager, AssetManager assetManager) {
        this.assetLoadManager = assetLoadManager;
        this.assetManager = assetManager;
    }

    public AssetLoadManager getAssetLoadManager() {
        return assetLoadManager;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }
}
