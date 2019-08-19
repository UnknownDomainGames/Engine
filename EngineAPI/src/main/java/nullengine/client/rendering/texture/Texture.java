package nullengine.client.rendering.texture;

public interface Texture {

    int getWidth();

    int getHeight();

    void bind();

    void dispose();
}
