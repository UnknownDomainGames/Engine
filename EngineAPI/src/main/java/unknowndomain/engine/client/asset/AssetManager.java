package unknowndomain.engine.client.asset;

import unknowndomain.engine.util.Disposable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface AssetManager extends Disposable {

    @Nonnull
    <T extends Asset> AssetType<T> createType(@Nonnull Class<T> assetClass, @Nonnull AssetFactory<T> factory);

    @Nonnull
    <T extends Asset> AssetType<T> createType(@Nonnull Class<T> assetClass, @Nonnull String name, @Nonnull AssetFactory<T> factory);

    @Nonnull
    <T extends Asset> AssetType<T> createType(@Nonnull Class<T> assetClass, @Nonnull String name, @Nullable String fileNameExtension, @Nonnull AssetFactory<T> factory);

    @Nullable
    <T extends Asset> T get(@Nonnull AssetType<T> type, @Nonnull AssetPath path);

    @Nullable
    <T extends Asset> T load(@Nonnull AssetType<T> type, @Nonnull AssetPath path);

    @Nonnull
    AssetSourceManager getSourceManager();

    void cleanUnuseCache();
}
