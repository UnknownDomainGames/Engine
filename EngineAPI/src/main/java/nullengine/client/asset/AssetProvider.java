package nullengine.client.asset;

import nullengine.client.asset.exception.AssetLoadException;

import javax.annotation.Nonnull;

public interface AssetProvider<T> {

    void init(AssetManager manager, AssetType<T> type);

    void register(Asset<T> asset);

    /**
     * @throws AssetLoadException
     */
    @Nonnull
    T load(AssetPath path);

    void dispose(Asset<T> asset);

    void dispose();
}
