package nullengine.client.asset.reloading;

@FunctionalInterface
public interface AssetReloadListener {
    void onReload(AssetReloadScheduler scheduler);
}
