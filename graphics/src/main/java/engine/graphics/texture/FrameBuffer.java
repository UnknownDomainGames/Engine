package engine.graphics.texture;

import org.joml.Vector4ic;

public interface FrameBuffer {

    int getId();

    int getWidth();

    int getHeight();

    void bind();

    void bindReadOnly();

    void bindDrawOnly();

    void copyFrom(FrameBuffer src, boolean copyColor, boolean copyDepth, boolean copyStencil,
                  FilterMode filterMode);

    void copyFrom(FrameBuffer src, Vector4ic srcRect, Vector4ic destRect,
                  boolean copyColor, boolean copyDepth, boolean copyStencil, FilterMode filterMode);

    void dispose();

    boolean isDisposed();
}
