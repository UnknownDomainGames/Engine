package engine.graphics.image;

import engine.util.Color;

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

    void setImage(int x, int y, ByteBuffer src, int srcWidth, int srcHeight, int srcMinX, int srcMinY, int srcMaxX, int srcMaxY);

    default void setImage(int x, int y, ByteBuffer src, int srcWidth, int srcHeight) {
        setImage(x, y, src, srcWidth, srcHeight, 0, 0, srcWidth, srcHeight);
    }

    default void setImage(int x, int y, ReadOnlyImage src) {
        setImage(x, y, src.getPixelBuffer(), src.getWidth(), src.getHeight());
    }

    default void setImage(int x, int y, ReadOnlyImage src, int srcMinX, int srcMinY, int srcMaxX, int srcMaxY) {
        setImage(x, y, src.getPixelBuffer(), src.getWidth(), src.getHeight(), srcMinX, srcMinY, srcMaxX, srcMaxY);
    }

    default void fill(Color color) {
        fill(color.toRGBA());
    }

    void fill(int rgba);
}
