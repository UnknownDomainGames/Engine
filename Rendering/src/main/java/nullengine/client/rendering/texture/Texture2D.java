package nullengine.client.rendering.texture;

public interface Texture2D extends Texture {

    int getWidth();

    int getHeight();

    float getMinU();

    float getMinV();

    float getMaxU();

    float getMaxV();
}
