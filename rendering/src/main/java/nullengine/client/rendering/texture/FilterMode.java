package nullengine.client.rendering.texture;

public enum FilterMode {
    LINEAR(false),
    NEAREST(false),
    NEAREST_MIPMAP_NEAREST(true),
    LINEAR_MIPMAP_NEAREST(true),
    NEAREST_MIPMAP_LINEAR(true),
    LINEAR_MIPMAP_LINEAR(true);

    public final boolean mipmap;

    FilterMode(boolean mipmap) {
        this.mipmap = mipmap;
    }
}
