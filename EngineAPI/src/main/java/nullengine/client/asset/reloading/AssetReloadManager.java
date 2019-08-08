package nullengine.client.asset.reloading;

public interface AssetReloadManager {

    void addFirst(String name, AssetReloadListener listener);

    void addLast(String name, AssetReloadListener listener);

    void addBefore(String name, String nextListener, AssetReloadListener listener);

    void addAfter(String name, String previousListener, AssetReloadListener listener);

    void reload();
}
