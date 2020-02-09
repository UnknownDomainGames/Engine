package engine.graphics.texture;

public interface Texture {

    TextureFormat getFormat();

    int getId();

    void bind();

    void dispose();
}
