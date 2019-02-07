package unknowndomain.engine.client.asset;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class AssetPath {

    public static final char SEPARATOR = '/';

    public static AssetPath of(@Nullable AssetPath parent, @Nonnull String first, @Nonnull String... others) {
        return of(parent == null ? first : parent.getRealPath() + SEPARATOR + first, others);
    }

    public static AssetPath of(@Nonnull String first, @Nonnull String... others) {
        StringBuilder builder = new StringBuilder(first);
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
    public String toString() {
        return path;
    }
}
