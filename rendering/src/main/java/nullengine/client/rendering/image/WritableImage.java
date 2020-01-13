package nullengine.client.rendering.image;

import nullengine.util.Color;

import java.nio.ByteBuffer;

public interface WritableImage extends ReadOnlyImage {
    ByteBuffer getWritablePixelBuffer();

    int getPixel(int x, int y);

    default void setPixel(int x, int y, Color color) {
        setPixel(x, y, color.toRGBA());
    }

    void setPixel(int x, int y, int rgba);

    default void setPixel(int x, int y, int width, int height, Color color) {
        setPixel(x, y, width, height, color.toRGBA());
    }

    void setPixel(int x, int y, int width, int height, int rgba);

    void setImage(int x, int y, ByteBuffer pixelBuffer, int u, int v, int width, int height);

    default void setImage(int x, int y, ByteBuffer pixelBuffer, int width, int height) {
        setImage(x, y, pixelBuffer, width, height, 0, 0);
    }

    default void setImage(int x, int y, ReadOnlyImage image) {
        setImage(x, y, image.getPixelBuffer(), image.getWidth(), image.getHeight());
    }

    default void setImage(int x, int y, ReadOnlyImage image, int u, int v) {
        setImage(x, y, image.getPixelBuffer(), image.getWidth(), image.getHeight(), u, v);
    }

    default void fill(Color color) {
        fill(color.toRGBA());
    }

    void fill(int rgba);
}
