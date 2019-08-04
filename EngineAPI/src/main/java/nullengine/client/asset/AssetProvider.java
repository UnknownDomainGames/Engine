package nullengine.client.asset;

import nullengine.client.asset.exception.AssetLoadException;

import javax.annotation.Nonnull;

public interface AssetProvider<T> {

    void init(AssetManager manager, AssetType<T> type);

    void register(Asset<T> asset);

    void unregister(Asset<T> asset);

    /**
     * @throws AssetLoadException
     */
    @Nonnull
    T loadDirect(AssetPath path);

    void dispose();
}
