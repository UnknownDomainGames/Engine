package nullengine.client.rendering.texture;

import com.github.mouse0w0.observable.value.ObservableValue;
import nullengine.client.asset.AssetPath;
import nullengine.exception.UninitializationException;

import java.util.function.Supplier;

public interface TextureManager {

    ObservableValue<GLTexture> getTexture(AssetPath path);

    GLTexture getTextureDirect(AssetPath path);

    GLTexture getTextureDirect(TextureBuffer buffer);

    TextureAtlasPart addTextureToAtlas(AssetPath path, TextureAtlasName type);

    TextureAtlas getTextureAtlas(TextureAtlasName type);

    void reloadTextureAtlas(TextureAtlasName type);

    void reload();

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
