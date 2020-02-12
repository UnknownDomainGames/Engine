package engine.graphics.texture;

import javax.annotation.Nullable;

public interface RenderBuffer {
    int getId();

    TextureFormat getFormat();

    boolean isMultiSample();

    @Nullable
    Sampler getSampler();

    int getWidth();

    int getHeight();

    void bind();

    void dispose();

    boolean isDisposed();

}
