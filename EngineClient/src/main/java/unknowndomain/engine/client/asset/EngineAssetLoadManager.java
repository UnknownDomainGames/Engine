package unknowndomain.engine.client.asset;

import unknowndomain.engine.client.asset.exception.AssetLoadException;
import unknowndomain.engine.client.asset.exception.AssetNotFoundException;
import unknowndomain.engine.client.asset.loader.AssetLoadManager;
import unknowndomain.engine.client.asset.loader.AssetLoader;
import unknowndomain.engine.client.asset.loader.AssetType;
import unknowndomain.engine.client.asset.source.AssetSource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.Validate.notEmpty;

public class EngineAssetLoadManager implements AssetLoadManager {

    private final AssetManager assetManager;

    private final Map<String, AssetType<?>> registeredTypes = new HashMap<>();

    public EngineAssetLoadManager(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    @Nonnull
    @Override
    public <T> AssetType<T> createType(@Nonnull Class<T> assetClass, @Nonnull AssetLoader<T> loader) {
        return createType(assetClass, assetClass.getSimpleName(), loader);
    }

    @Nonnull
    @Override
    public <T> AssetType<T> createType(@Nonnull Class<T> assetClass, @Nonnull String name, @Nonnull AssetLoader<T> loader) {
        requireNonNull(assetClass);
        notEmpty(name);
        requireNonNull(loader);

        if (registeredTypes.containsKey(name)) {
            throw new IllegalArgumentException(String.format("AssetType %s has been registered.", name));
        }

        AssetType<T> type = new AssetType<>(assetClass, name, loader);
        registeredTypes.put(name, type);
        return type;
    }

    @Override
    public Optional<AssetType<?>> getType(String name) {
        return Optional.ofNullable(registeredTypes.get(name));
    }

    @Override
    public Collection<AssetType<?>> getSupportedTypes() {
        return registeredTypes.values();
    }

    @Nullable
    @Override
    public <T> T load(@Nonnull AssetType<T> type, @Nonnull AssetPath path) {
        requireNonNull(type);
        requireNonNull(path);
        if (!registeredTypes.containsKey(type)) {
            throw new IllegalStateException(String.format("AssetType %s has not been registered.", type.getName()));
        }

        return internalLoad(type, path);
    }

    @Override
    public <T> CompletableFuture<T> loadAsync(@Nonnull AssetType<T> type, @Nonnull AssetPath path) {
        return null;
    }

    protected <T> T internalLoad(@Nonnull AssetType<T> type, @Nonnull AssetPath path) {
        Optional<AssetSource> source = getAssetManager().getSource(path);
        if (source.isEmpty()) {
            throw new AssetNotFoundException(path);
        }

        try (InputStream input = source.get().openStream(path)) {
            return type.getLoader().load(type, path, input);
        } catch (Exception e) {
            throw new AssetLoadException(String.format("Cannot load asset because of catch a exception. Path: %s, Type: %s", path.getRealPath(), type.getName()), e);
        }
    }

    @Nonnull
    public AssetManager getAssetManager() {
        return assetManager;
    }
}
