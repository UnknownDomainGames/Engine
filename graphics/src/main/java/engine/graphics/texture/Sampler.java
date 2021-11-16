package engine.graphics.texture;

import engine.graphics.GraphicsEngine;
import engine.graphics.util.DepthCompareMode;
import engine.util.Color;

public interface Sampler {
    static Sampler.Builder builder() {
        return GraphicsEngine.getGraphicsBackend().getResourceFactory().createSamplerBuilder();
    }

    int getId();

    void dispose();

    boolean isDisposed();

    interface Builder {
        Builder magFilter(FilterMode filterMode);

        Builder minFilter(FilterMode filterMode);

        Builder wrapS(WrapMode wrapMode);

        Builder wrapT(WrapMode wrapMode);

        Builder wrapR(WrapMode wrapMode);

        Builder minLod(float min);

        Builder maxLod(float max);

        Builder borderColor(Color color);

        Builder depthCompareMode(DepthCompareMode depthCompareMode);

        Sampler build();
    }
}
