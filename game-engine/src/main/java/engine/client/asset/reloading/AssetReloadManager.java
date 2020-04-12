package engine.client.asset.reloading;

public interface AssetReloadManager {

    void addHandler(AssetReloadHandler handler);

    void reload();
}
