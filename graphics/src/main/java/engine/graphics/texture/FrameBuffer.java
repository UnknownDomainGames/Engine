package engine.graphics.texture;

import engine.graphics.GraphicsEngine;
import org.joml.Vector4ic;

import javax.annotation.Nullable;

public interface FrameBuffer {
    static Builder builder() {
        return GraphicsEngine.getGraphicsBackend().getResourceFactory().createFrameBufferBuilder();
    }

    int getId();

    Attachable getAttachable(Attachment attachment);

    int getWidth();

    int getHeight();

    void resize(int width, int height);

    void bind();

    void bindReadOnly();

    void bindDrawOnly();

    void copyFrom(FrameBuffer source, boolean copyColor, boolean copyDepth, boolean copyStencil,
                  FilterMode filterMode);

    void copyFrom(FrameBuffer source, Vector4ic sourceRect, Vector4ic destRect,
                  boolean copyColor, boolean copyDepth, boolean copyStencil, FilterMode filterMode);

    void dispose();

    boolean isDisposed();

    enum Attachment {
        COLOR_0,
        COLOR_1,
        COLOR_2,
        COLOR_3,
        COLOR_4,
        COLOR_5,
        COLOR_6,
        COLOR_7,
        COLOR_8,
        COLOR_9,
        COLOR_10,
        COLOR_11,
        COLOR_12,
        COLOR_13,
        COLOR_14,
        COLOR_15,
        COLOR_16,
        COLOR_17,
        COLOR_18,
        COLOR_19,
        COLOR_20,
        COLOR_21,
        COLOR_22,
        COLOR_23,
        COLOR_24,
        COLOR_25,
        COLOR_26,
        COLOR_27,
        COLOR_28,
        COLOR_29,
        COLOR_30,
        COLOR_31,
        DEPTH,
        STENCIL,
        DEPTH_STENCIL
    }

    interface Attachable {
        TextureFormat getFormat();

        int getId();

        boolean isMultiSample();

        @Nullable
        Sampler getSampler();

        int getWidth();

        int getHeight();

        void bind();

        void dispose();

        boolean isDisposed();
    }

    @FunctionalInterface
    interface AttachableFactory {
        FrameBuffer.Attachable create(int width, int height);
    }

    interface Builder {
        Builder attach(Attachment attachment, Texture2D.Builder builder);

        Builder attach(Attachment attachment, RenderBuffer.Builder builder);

        Builder attach(Attachment attachment, AttachableFactory factory);

        FrameBuffer build(int width, int height);
    }
}
