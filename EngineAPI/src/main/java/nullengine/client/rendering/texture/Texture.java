package nullengine.client.rendering.texture;

public interface Texture {

    int getWidth();

    int getHeight();

    float getMinU();

    float getMinV();

    float getMaxU();

    float getMaxV();

    void bind();

    void dispose();
}
