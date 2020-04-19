package engine.client.asset;

import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class Asset<T> {

    private final AssetType<T> type;
    private final AssetURL url;

    private T value;

    private volatile boolean disposed;

    @Nonnull
    public static <T> Asset<T> create(@Nonnull AssetType<T> type, @Nonnull AssetURL url) {
        return AssetManager.instance().create(type, url);
    }

    @Nonnull
    public static <T> Asset<T> create(@Nonnull AssetType<T> type, @Nonnull String location) {
        return AssetManager.instance().create(type, AssetURL.of(location));
    }

    @Nonnull
    public static <T> Asset<T> create(@Nonnull AssetType<T> type, @Nonnull String domain, @Nonnull String location) {
        return AssetManager.instance().create(type, AssetURL.of(domain, location));
    }

    Asset(@Nonnull AssetType<T> type, @Nonnull AssetURL url) {
        this.type = Validate.notNull(type);
        this.url = Validate.notNull(url);
    }

    public T get() {
        return value;
    }

    public boolean isLoaded() {
        return value != null;
    }

    public boolean isEmpty() {
        return value == null;
    }

    @Nonnull
    public AssetType<T> getType() {
        return type;
    }

    @Nonnull
    public AssetURL getUrl() {
        return url;
    }

    public synchronized void reload() {
        if (disposed)
            throw new IllegalStateException("Asset has been disposed.");

        value = type.getProvider().loadDirect(url);
    }

    public boolean isDisposed() {
        return disposed;
    }

    public synchronized void dispose() {
        if (disposed)
            return;

        disposed = true;
        type.getProvider().unregister(this);
        value = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Asset<?> asset = (Asset<?>) o;
        return url.equals(asset.url) &&
                type.equals(asset.type);
    }

    @Override
    public int hashCode() {
        return url.hashCode() * 31 + type.hashCode();
    }

    @Override
    public String toString() {
        return "Asset{" +
                "url=" + url +
                ", type=" + type +
                '}';
    }
}
