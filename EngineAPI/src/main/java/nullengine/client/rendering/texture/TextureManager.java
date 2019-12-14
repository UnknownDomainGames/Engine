package nullengine.client.rendering.texture;

import nullengine.client.rendering.gl.texture.GLTexture2D;

import java.util.function.Supplier;

public interface TextureManager {

    GLTexture2D getWhiteTexture();

    TextureAtlas getDefaultAtlas();

    static TextureManager instance() {
        return Internal.instance.get();
    }

    class Internal {
        private static Supplier<TextureManager> instance = () -> {
            throw new IllegalStateException("TextureManager is uninitialized");
        };

        public static void setInstance(TextureManager instance) {
            Internal.instance = () -> instance;
        }
    }
}
