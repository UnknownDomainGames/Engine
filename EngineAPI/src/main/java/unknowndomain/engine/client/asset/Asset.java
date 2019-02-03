package unknowndomain.engine.client.asset;

public abstract class Asset {

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
