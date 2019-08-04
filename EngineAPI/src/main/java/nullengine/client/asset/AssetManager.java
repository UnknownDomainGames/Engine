package nullengine.client.asset;

import nullengine.client.asset.exception.AssetLoadException;
import nullengine.client.asset.reloading.AssetReloadManager;
import nullengine.client.asset.source.AssetSourceManager;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;

public interface AssetManager {

    <T> AssetType<T> register(@Nonnull Class<T> assetClass, @Nonnull AssetProvider<T> provider);

    <T> AssetType<T> register(@Nonnull Class<T> assetClass, @Nonnull String name, @Nonnull AssetProvider<T> provider);

    Optional<AssetType<?>> getType(String name);

    boolean hasType(String name);

    Collection<AssetType<?>> getSupportedTypes();

    /**
     * @throws AssetLoadException ;
     */
    @Nonnull
    <T> Asset<T> create(@Nonnull AssetType<T> type, @Nonnull AssetPath path);

    /**
     * @throws AssetLoadException;
     */
    @Nonnull
    <T> T loadDirect(@Nonnull AssetType<T> type, @Nonnull AssetPath path);

    AssetSourceManager getSourceManager();

    AssetReloadManager getReloadManager();

    void reload() throws InterruptedException;
}
