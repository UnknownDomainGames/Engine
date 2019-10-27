package nullengine.client.rendering.texture;

public interface Texture2D {

    void bind();

    void dispose();

    int getWidth();

    int getHeight();

    float getMinU();

    float getMinV();

    float getMaxU();

    float getMaxV();
}
