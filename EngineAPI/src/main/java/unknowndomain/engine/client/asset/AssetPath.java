package unknowndomain.engine.client.asset;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AssetPath {

    private final AssetPath parent;
    private final String path;
    private final String fullPath;

    public AssetPath(@Nonnull String path) {
        this(null, path);
    }

    public AssetPath(@Nullable AssetPath parent, @Nonnull String path) {
        this.parent = parent;
        this.path = path;
        this.fullPath = parent == null ? parent.getFullPath().concat(path) : path;
    }

    public AssetPath getParent() {
        return parent;
    }

    public String getPath() {
        return path;
    }

    public String getFullPath() {
        return fullPath;
    }
}
