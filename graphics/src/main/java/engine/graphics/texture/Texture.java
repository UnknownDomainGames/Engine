package engine.graphics.texture;

import javax.annotation.Nullable;

public interface Texture {

    int getId();

    TextureFormat getFormat();

    boolean isMultiSample();

    @Nullable
    Sampler getSampler();

    void bind();

    void dispose();

    boolean isDisposed();
}
