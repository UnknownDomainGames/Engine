package engine.graphics.texture;

public interface Texture {

    TextureFormat getFormat();

    void bind();

    void dispose();
}
