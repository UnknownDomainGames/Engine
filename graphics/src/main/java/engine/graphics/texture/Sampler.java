package engine.graphics.texture;

import engine.util.Color;

public interface Sampler {
    int getId();

    Sampler setMagFilter(FilterMode filterMode);

    Sampler setMinFilter(FilterMode filterMode);

    Sampler setWrapMode(WrapMode wrapMode);

    Sampler setMinLod(float min);

    Sampler setMaxLod(float max);

    Sampler setBorderColor(Color color);

    Sampler setCompareMode(CompareMode compareMode);

    void dispose();

    boolean isDisposed();
}
