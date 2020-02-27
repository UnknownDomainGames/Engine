package engine.graphics.texture;

import engine.graphics.util.DepthCompareMode;
import engine.util.Color;

public interface Sampler {
    int getId();

    void bind(int unit);

    Sampler setMagFilter(FilterMode filterMode);

    Sampler setMinFilter(FilterMode filterMode);

    Sampler setWrapMode(WrapMode wrapMode);

    Sampler setMinLod(float min);

    Sampler setMaxLod(float max);

    Sampler setBorderColor(Color color);

    Sampler setDepthCompareMode(DepthCompareMode depthCompareMode);

    void dispose();

    boolean isDisposed();
}
