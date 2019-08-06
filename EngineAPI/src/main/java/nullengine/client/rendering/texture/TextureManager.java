package nullengine.client.rendering.texture;

import nullengine.client.asset.AssetURL;
import nullengine.exception.UninitializationException;

import java.util.function.Supplier;

public interface TextureManager {

    GLTexture getTextureDirect(TextureBuffer buffer);

    TextureAtlasPart addTextureToAtlas(AssetURL path, TextureAtlasName type);

    TextureAtlas getTextureAtlas(TextureAtlasName type);

    void reloadTextureAtlas(TextureAtlasName type);

    GLTexture getWhiteTexture();

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
