package unknowndomain.engine.client.asset;

import unknowndomain.engine.util.Disposable;

public abstract class Asset implements Disposable {

    private final AssetPath path;
    private final AssetType<?> type;

    public Asset(AssetPath path, AssetType<?> type) {
        this.path = path;
        this.type = type;
    }

    public AssetPath getPath() {
        return path;
    }

    public AssetType<?> getType() {
        return type;
    }
}
