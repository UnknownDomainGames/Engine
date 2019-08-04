package nullengine.client.asset;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Objects;

@ThreadSafe
public final class Asset<T> {

    private final AssetType<T> type;
    private final AssetPath path;

    private T value;

    private volatile boolean disposed;

    Asset(@Nonnull AssetType<T> type, @Nonnull AssetPath path) {
        this.type = Objects.requireNonNull(type);
        this.path = Objects.requireNonNull(path);
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
    public AssetPath getPath() {
        return path;
    }

    public synchronized void reload() {
        if (disposed)
            throw new IllegalStateException("Asset has been disposed.");

        value = type.getProvider().loadDirect(path);
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
        return path.equals(asset.path) &&
                type.equals(asset.type);
    }

    @Override
    public int hashCode() {
        return path.hashCode() * 31 + type.hashCode();
    }

    @Override
    public String toString() {
        return "Asset{" +
                "path=" + path +
                ", type=" + type +
                '}';
    }
}
