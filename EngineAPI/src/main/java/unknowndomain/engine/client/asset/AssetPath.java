package unknowndomain.engine.client.asset;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class AssetPath {

    public static final char SEPARATOR = '/';

    public static AssetPath of(@Nonnull String first, String... others) {
        StringBuilder builder = new StringBuilder(first);
        for (String other : others) {
            builder.append(SEPARATOR).append(other);
        }
        return of(null, builder.toString());
    }

    public static AssetPath of(@Nullable AssetPath parent, @Nonnull String path) {
        return new AssetPath(parent == null ? path : parent.getPath() + SEPARATOR + path);
    }

    private final String path;

    private AssetPath(@Nonnull String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return path;
    }
}
