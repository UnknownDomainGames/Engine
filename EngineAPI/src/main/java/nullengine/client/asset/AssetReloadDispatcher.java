package nullengine.client.asset;

public interface AssetReloadDispatcher {

    void addFirst(String name, AssetReloadListener listener);

    void addLast(String name, AssetReloadListener listener);

    void addBefore(String name, String nextListener, AssetReloadListener listener);

    void addAfter(String name, String previousListener, AssetReloadListener listener);

    void dispatchReload();
}
