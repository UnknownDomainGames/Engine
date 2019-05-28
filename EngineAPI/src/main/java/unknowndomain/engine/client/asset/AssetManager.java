package unknowndomain.engine.client.asset;

import unknowndomain.engine.client.asset.exception.AssetLoadException;
import unknowndomain.engine.client.asset.source.AssetSourceManager;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;

public interface AssetManager {

    <T> AssetType<T> register(@Nonnull Class<T> assetClass, @Nonnull AssetProvider<T> provider);

    <T> AssetType<T> register(@Nonnull Class<T> assetClass, @Nonnull String name, @Nonnull AssetProvider<T> provider);

    Optional<AssetType<?>> getType(String name);

    Collection<AssetType<?>> getSupportedTypes();

    /**
     * @throws AssetLoadException;
     */
    @Nonnull
    <T> Asset<T> create(@Nonnull AssetType<T> type, @Nonnull AssetPath path);

    /**
     * @throws AssetLoadException;
     */
    @Nonnull
    <T> T loadDirect(@Nonnull AssetType<T> type, @Nonnull AssetPath path);

    AssetSourceManager getSourceManager();

    AssetReloadDispatcher getReloadDispatcher();

//    /**
//     * @throws AssetLoadException;
//     */
//    @Nonnull
//    <T> CompletableFuture<T> loadAsync(@Nonnull AssetType<T> type, @Nonnull AssetPath path);

    void reload();
}
