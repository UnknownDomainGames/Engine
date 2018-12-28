package unknowndomain.engine.client.asset;

public interface AssetManager {
    <T extends Asset> AssetType<T> createType(Class<T> assetClass, AssetFactory<T> factory);

    <T extends Asset> AssetType<T> createType(Class<T> assetClass, String name, AssetFactory<T> factory);

    AssetSourceManager getSourceManager();
}
