package unknowndomain.engine.client.asset;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public final class AssetPath {

    public static final char SEPARATOR = '/';

    public static AssetPath of(@Nullable AssetPath parent, @Nonnull String first, @Nonnull String... others) {
        return of(parent == null ? first : parent.getRealPath() + SEPARATOR + first, others);
    }

    public static AssetPath of(@Nonnull String first, @Nonnull String... others) {
        StringBuilder builder = new StringBuilder(Objects.requireNonNull(first));
        for (String other : others) {
            builder.append(SEPARATOR).append(other);
        }
        return new AssetPath(builder.toString());
    }

    private final String path;

    protected AssetPath(@Nonnull String path) {
        this.path = path;
    }

    public String getRealPath() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssetPath assetPath = (AssetPath) o;
        return path.equals(assetPath.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

    @Override
    public String toString() {
        return path;
    }
}
