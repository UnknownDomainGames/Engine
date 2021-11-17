package engine.graphics.texture;

import engine.graphics.GraphicsEngine;
import engine.graphics.image.ReadOnlyImage;
import engine.util.Color;
import org.joml.Vector2ic;

import java.nio.ByteBuffer;

public interface Texture2D extends Texture {

    static Builder builder() {
        return GraphicsEngine.getGraphicsBackend().getResourceFactory().createTexture2DBuilder();
    }

    static Texture2D white() {
        return GraphicsEngine.getGraphicsBackend().getResourceFactory().getWhiteTexture2D();
    }

    int getWidth();

    int getHeight();

    void upload(int level, ReadOnlyImage image);

    void upload(int level, int offsetX, int offsetY, ReadOnlyImage image);

    void upload(int level, int width, int height, ByteBuffer pixels);

    void upload(int level, int offsetX, int offsetY, int width, int height, ByteBuffer pixels);

    interface Builder {
        Builder format(ColorFormat format);

        Builder magFilter(FilterMode mode);

        Builder minFilter(FilterMode mode);

        Builder wrapS(WrapMode mode);

        Builder wrapT(WrapMode mode);

        Builder borderColor(Color color);

        Builder generateMipmap();

        Texture2D build();

        Texture2D build(Vector2ic size);

        Texture2D build(int width, int height);

        Texture2D build(ReadOnlyImage image);

        Texture2D build(ByteBuffer pixelBuffer, int width, int height);
    }
}
