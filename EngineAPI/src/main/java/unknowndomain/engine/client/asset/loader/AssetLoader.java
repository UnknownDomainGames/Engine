package unknowndomain.engine.client.asset.loader;

import unknowndomain.engine.client.asset.AssetPath;
import unknowndomain.engine.util.Disposable;

import javax.annotation.Nonnull;
import java.io.InputStream;

public interface AssetLoader<T> extends Disposable {

    @Nonnull
    T load(AssetType<T> type, AssetPath path, InputStream input);
}
