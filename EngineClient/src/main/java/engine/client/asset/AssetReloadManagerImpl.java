package engine.client.asset;

import engine.client.asset.reloading.AssetReloadListener;
import engine.client.asset.reloading.AssetReloadManager;
import engine.util.SortedList;

import java.util.List;

public class AssetReloadManagerImpl implements AssetReloadManager {

    private final List<AssetReloadListener> listeners = SortedList.create();

    @Override
    public void addListener(AssetReloadListener listener) {
        listeners.add(listener);
    }

    @Override
    public void reload() {
        listeners.forEach(AssetReloadListener::onReload);
    }
}
