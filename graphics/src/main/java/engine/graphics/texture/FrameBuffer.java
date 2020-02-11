package engine.graphics.texture;

public interface FrameBuffer {
    int getId();

    int getWidth();

    int getHeight();

    void resize(int width, int height);

    void bind();

    void bindReadOnly();

    void bindDrawOnly();

    void dispose();

    boolean isDisposed();
}
