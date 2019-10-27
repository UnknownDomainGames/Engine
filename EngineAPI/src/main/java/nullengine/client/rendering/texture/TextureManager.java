package nullengine.client.rendering.texture;

import nullengine.client.rendering.gl.texture.GLTexture2D;
import nullengine.exception.UninitializationException;

import java.util.function.Supplier;

public interface TextureManager {

    GLTexture2D getWhiteTexture();

    GLTexture2D getTextureDirect(Texture2DBuffer buffer);

    Texture2DAtlas getTextureAtlas(TextureAtlasName type);

    static TextureManager instance() {
        return Internal.instance.get();
    }

    class Internal {
        private static Supplier<TextureManager> instance = UninitializationException.supplier("TextureManager is uninitialized");

        public static void setInstance(TextureManager instance) {
            Internal.instance = () -> instance;
        }
    }
}
