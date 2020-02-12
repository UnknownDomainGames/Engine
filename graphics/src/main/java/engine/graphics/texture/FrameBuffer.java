package engine.graphics.texture;

import org.joml.Vector4ic;

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
}
