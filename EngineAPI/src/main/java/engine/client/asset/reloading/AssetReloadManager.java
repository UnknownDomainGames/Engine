package engine.client.asset.reloading;

public interface AssetReloadManager {

    void addListener(AssetReloadListener listener);

    void reload();
}
