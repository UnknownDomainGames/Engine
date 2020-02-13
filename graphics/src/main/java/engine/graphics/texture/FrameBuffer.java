package engine.graphics.texture;

import org.joml.Vector4ic;

import javax.annotation.Nullable;

public interface FrameBuffer {
    int getId();

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
}
