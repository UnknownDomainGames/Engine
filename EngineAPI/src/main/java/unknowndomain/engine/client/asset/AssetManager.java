package unknowndomain.engine.client.asset;

import unknowndomain.engine.client.asset.exception.AssetLoadException;
import unknowndomain.engine.util.Disposable;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface AssetManager extends Disposable {

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

    void reload();
}
