package nullengine.client.asset.reloading;

public interface AssetReloadManager {

    default void addFirst(String name, Runnable listener) {
        addFirst(name, scheduler -> listener.run());
    }

    void addFirst(String name, AssetReloadListener listener);

    default void addLast(String name, Runnable listener) {
        addLast(name, scheduler -> listener.run());
    }

    void addLast(String name, AssetReloadListener listener);

    default void addBefore(String name, String nextListener, Runnable listener) {
        addBefore(name, nextListener, scheduler -> listener.run());
    }

    void addBefore(String name, String nextListener, AssetReloadListener listener);

    default void addAfter(String name, String previousListener, Runnable listener) {
        addAfter(name, previousListener, scheduler -> listener.run());
    }

    void addAfter(String name, String previousListener, AssetReloadListener listener);

    void reload() throws InterruptedException;
}
