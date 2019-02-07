package unknowndomain.engine.client.event.asset;

import unknowndomain.engine.client.asset.AssetManager;
import unknowndomain.engine.client.asset.source.AssetSourceManager;
import unknowndomain.engine.event.Event;

public class AssetLoadEvent implements Event {

    private final AssetManager assetManager;
    private final AssetSourceManager assetSourceManager;

    public AssetLoadEvent(AssetManager assetManager, AssetSourceManager assetSourceManager) {
        this.assetManager = assetManager;
        this.assetSourceManager = assetSourceManager;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public AssetSourceManager getAssetSourceManager() {
        return assetSourceManager;
    }
}
