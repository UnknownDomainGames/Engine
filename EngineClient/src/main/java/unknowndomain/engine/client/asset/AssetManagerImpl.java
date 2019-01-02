package unknowndomain.engine.client.asset;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.MapMaker;
import unknowndomain.engine.util.Disposable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.Validate.notEmpty;

public class AssetManagerImpl implements AssetManager {

    private final AssetSourceManager sourceManager = new AssetSourceManagerImpl();

    private final Map<String, AssetType<?>> registeredTypes = new HashMap<>();
    private final Map<AssetType<?>, Cache<AssetPath, ? extends Asset>> instanceAssets = new MapMaker().concurrencyLevel(1).makeMap();

    @Nonnull
    @Override
    public <T extends Asset> AssetType<T> createType(@Nonnull Class<T> assetClass, @Nonnull AssetFactory<T> factory) {
        return createType(assetClass, assetClass.getSimpleName(), factory);
    }

    @Nonnull
    @Override
    public <T extends Asset> AssetType<T> createType(@Nonnull Class<T> assetClass, @Nonnull String name, @Nonnull AssetFactory<T> factory) {
        return createType(assetClass, assetClass.getSimpleName(), null, factory);
    }

    @Nonnull
    @Override
    public <T extends Asset> AssetType<T> createType(@Nonnull Class<T> assetClass, @Nonnull String name, @Nullable String fileNameExtension, @Nonnull AssetFactory<T> factory) {
        requireNonNull(assetClass);
        notEmpty(name);
        requireNonNull(factory);

        if (registeredTypes.containsKey(name)) {
            throw new IllegalArgumentException(String.format("AssetType %s has been registered.", name));
        }

        AssetType<T> type = new AssetType<>(assetClass, name, fileNameExtension, factory);
        registeredTypes.put(name, type);
        createAssetCache(type);
        return type;
    }

    protected <T extends Asset> void createAssetCache(AssetType<T> type) {
        instanceAssets.put(type, CacheBuilder.newBuilder().weakValues().removalListener(notification -> {
            if (notification.getValue() != null) {
                ((Disposable) notification.getValue()).dispose();
            }
        }).build());
    }

    @Nullable
    @Override
    public <T extends Asset> T get(@Nonnull AssetType<T> type, @Nonnull AssetPath path) {
        requireNonNull(type);
        requireNonNull(path);
        if (!registeredTypes.containsKey(type)) {
            throw new IllegalStateException(String.format("AssetType %s has not been registered.", type.getName()));
        }

        Cache<AssetPath, T> assetCache = (Cache<AssetPath, T>) instanceAssets.get(type);
        try {
            return assetCache.get(path, () -> internalLoad(assetCache, type, path));
        } catch (ExecutionException ignored) {
            return null; // Unreachable code.
        }
    }

    @Nullable
    @Override
    public <T extends Asset> T load(@Nonnull AssetType<T> type, @Nonnull AssetPath path) {
        requireNonNull(type);
        requireNonNull(path);
        if (!registeredTypes.containsKey(type)) {
            throw new IllegalStateException(String.format("AssetType %s has not been registered.", type.getName()));
        }

        return internalLoad((Cache<AssetPath, T>) instanceAssets.get(type), type, path);
    }

    protected <T extends Asset> T internalLoad(Cache<AssetPath, T> assetCache, @Nonnull AssetType<T> type, @Nonnull AssetPath path) {
        Optional<AssetSource> source = getSourceManager().getSource(path);
        if (source.isEmpty()) {
            return null; // TODO: log it.
        }

        try (InputStream input = source.get().openStream(path)) {
            T asset = type.getFactory().build(type, path, input);
            assetCache.put(path, asset);
            return asset;
        } catch (Exception e) {
            // TODO: log it.
            e.printStackTrace();
        }
        return null;
    }

    @Nonnull
    @Override
    public AssetSourceManager getSourceManager() {
        return sourceManager;
    }

    @Override
    public void cleanUnuseCache() {
        for (Cache<?, ?> cache : instanceAssets.values()) {
            cache.cleanUp();
        }
    }

    @Override
    public void dispose() {
        for (Cache<?, ?> cache : instanceAssets.values()) {
            cache.invalidateAll();
        }
    }
}
