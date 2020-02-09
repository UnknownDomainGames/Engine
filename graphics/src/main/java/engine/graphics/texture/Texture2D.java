package engine.graphics.texture;

import engine.graphics.GraphicsEngine;
import engine.graphics.image.ReadOnlyImage;
import engine.util.Color;

public interface Texture2D extends Texture {

    static Builder builder() {
        return GraphicsEngine.getGraphicsBackend().getResourceFactory().createTexture2DBuilder();
    }

    int getWidth();

    int getHeight();

    interface Builder {
        Builder format(TextureFormat format);

        Builder magFilter(FilterMode mode);

        Builder minFilter(FilterMode mode);

        Builder wrapS(WrapMode mode);

        Builder wrapT(WrapMode mode);

        Builder generateMipmap();

        Builder borderColor(Color color);

        Texture2D build(ReadOnlyImage image);
    }
}
