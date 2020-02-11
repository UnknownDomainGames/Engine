package engine.graphics.texture;

import java.util.function.Supplier;

public interface TextureManager {

    Texture2D getWhiteTexture();

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
