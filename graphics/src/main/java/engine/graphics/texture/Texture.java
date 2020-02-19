package engine.graphics.texture;

public interface Texture {

    int getId();

    TextureFormat getFormat();

    boolean isMultiSample();

    int getSamples();

    void bind();

    void dispose();

    boolean isDisposed();
}
