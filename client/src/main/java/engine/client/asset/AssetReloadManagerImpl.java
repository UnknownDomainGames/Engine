package engine.client.asset;

import engine.client.asset.reloading.AssetReloadHandler;
import engine.client.asset.reloading.AssetReloadManager;
import engine.util.SortedList;

import java.util.List;

public class AssetReloadManagerImpl implements AssetReloadManager {

    private final List<AssetReloadHandler> handlers = SortedList.create();

    @Override
    public void addHandler(AssetReloadHandler handler) {
        handlers.add(handler);
    }

    @Override
    public void reload() {
        handlers.forEach(AssetReloadHandler::doReload);
    }
}
