package unknowndomain.engine.client.asset.loader;

import unknowndomain.engine.client.asset.AssetPath;
import unknowndomain.engine.client.asset.exception.AssetLoadException;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface AssetLoadManager {

    @Nonnull
    <T> AssetType<T> createType(@Nonnull Class<T> assetClass, @Nonnull AssetLoader<T> loader);

    @Nonnull
    <T> AssetType<T> createType(@Nonnull Class<T> assetClass, @Nonnull String name, @Nonnull AssetLoader<T> loader);

    Optional<AssetType<?>> getType(@Nonnull String name);

    Collection<AssetType<?>> getSupportedTypes();

    @Nonnull
    <T> T load(@Nonnull AssetType<T> type, @Nonnull AssetPath path) throws AssetLoadException;

    @Nonnull
    <T> CompletableFuture<T> loadAsync(@Nonnull AssetType<T> type, @Nonnull AssetPath path) throws AssetLoadException;
}
