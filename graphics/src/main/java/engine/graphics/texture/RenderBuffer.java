package engine.graphics.texture;

import engine.graphics.GraphicsEngine;

import javax.annotation.Nullable;

public interface RenderBuffer extends FrameBuffer.Attachable {

    static RenderBuffer create(TextureFormat format, int width, int height) {
        return GraphicsEngine.getGraphicsBackend().getResourceFactory().createRenderBuffer(format, width, height);
    }

    static RenderBuffer create(TextureFormat format, Sampler sampler, int width, int height) {
        return GraphicsEngine.getGraphicsBackend().getResourceFactory().createRenderBuffer(format, sampler, width, height);
    }

    static Builder builder() {
        return GraphicsEngine.getGraphicsBackend().getResourceFactory().createRenderBufferBuilder();
    }

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

    interface Builder {
        Builder format(TextureFormat format);

        Builder sampler(Sampler sampler);

        RenderBuffer build(int width, int height);
    }
}
