package nullengine.client.asset.reloading;

public interface AssetReloadManager {

    void addListener(AssetReloadListener listener);

    void reload();
}
