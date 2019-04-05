package unknowndomain.engine.client.asset.loader;

import unknowndomain.engine.client.asset.AssetPath;

import javax.annotation.Nonnull;
import java.io.InputStream;

public interface AssetLoader<T> {

    @Nonnull
    T load(AssetType<T> type, AssetPath path, InputStream input);
}
