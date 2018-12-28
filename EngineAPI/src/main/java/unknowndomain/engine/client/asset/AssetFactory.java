package unknowndomain.engine.client.asset;

import unknowndomain.engine.util.Disposable;

public interface AssetFactory<T extends Asset> extends Disposable {

    void init(AssetSourceManager sourceManager);

    T getAsset(AssetType type, AssetPath path);
}
