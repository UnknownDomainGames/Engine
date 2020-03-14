package engine.graphics.texture;

public interface Texture {

    int getId();

    ColorFormat getFormat();

    boolean isMultiSample();

    int getSamples();

    void bind();

    void dispose();

    boolean isDisposed();
}
