package engine.client.asset;

import engine.client.asset.exception.AssetLoadException;
import engine.client.asset.reloading.AssetReloadManager;
import engine.client.asset.source.AssetSourceManager;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;

public interface AssetManager {

    <T> AssetType<T> register(@Nonnull AssetType<T> type);

    Optional<AssetType<?>> getType(String name);

    boolean hasType(String name);

    Collection<AssetType<?>> getSupportedTypes();

    /**
     * @throws AssetLoadException ;
     */
    @Nonnull
    <T> Asset<T> create(@Nonnull AssetType<T> type, @Nonnull AssetURL url);

    /**
     * @throws AssetLoadException;
     */
    @Nonnull
    <T> T loadDirect(@Nonnull AssetType<T> type, @Nonnull AssetURL url);

    AssetSourceManager getSourceManager();

    AssetReloadManager getReloadManager();

    void reload();

    static AssetManager instance() {
        return Internal.instanceSupplier.get();
    }

    class Internal {
        private static Supplier<AssetManager> instanceSupplier = () -> {
            throw new IllegalStateException("AssetManager is uninitialized");
        };

        public static void setInstance(AssetManager instance) {
            instanceSupplier = () -> instance;
        }
    }
}
