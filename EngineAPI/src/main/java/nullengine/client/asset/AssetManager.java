package nullengine.client.asset;

import nullengine.client.asset.exception.AssetLoadException;
import nullengine.client.asset.reloading.AssetReloadManager;
import nullengine.client.asset.source.AssetSourceManager;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;

public interface AssetManager {

    <T> AssetType<T> register(@Nonnull AssetType<T> type);

    Optional<AssetType<?>> getType(String name);

    boolean hasType(String name);

    Collection<AssetType<?>> getSupportedTypes();

    /**
     * @throws AssetLoadException ;
     */
    @Nonnull
    <T> Asset<T> create(@Nonnull AssetType<T> type, @Nonnull AssetURL path);

    /**
     * @throws AssetLoadException;
     */
    @Nonnull
    <T> T loadDirect(@Nonnull AssetType<T> type, @Nonnull AssetURL path);

    AssetSourceManager getSourceManager();

    AssetReloadManager getReloadManager();

    void reload();
}
