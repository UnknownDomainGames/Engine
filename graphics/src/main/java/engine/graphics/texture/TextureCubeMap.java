package engine.graphics.texture;

import engine.graphics.GraphicsEngine;

public interface TextureCubeMap extends Texture {

    static Builder builder() {
        return GraphicsEngine.getGraphicsBackend().getResourceFactory().createTextureCubeMapBuilder();
    }

    int getLength();

    interface Builder {
        Builder format(ColorFormat format);

        Builder magFilter(FilterMode mode);

        Builder minFilter(FilterMode mode);

        Builder wrapS(WrapMode mode);

        Builder wrapT(WrapMode mode);

        Builder wrapR(WrapMode mode);

        TextureCubeMap build(int length);
    }
}
