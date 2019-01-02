package unknowndomain.engine.client.asset;

import unknowndomain.engine.util.Disposable;

import java.io.InputStream;

public interface AssetFactory<T extends Asset> extends Disposable {

    T build(AssetType<T> type, AssetPath path, InputStream input);
}
