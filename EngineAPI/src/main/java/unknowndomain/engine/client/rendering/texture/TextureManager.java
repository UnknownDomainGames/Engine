package unknowndomain.engine.client.rendering.texture;

import unknowndomain.engine.client.asset.AssetPath;

public interface TextureManager {

    default GLTexture getTexture(AssetPath path) {
        return getTexture(path, false);
    }

    GLTexture getTexture(AssetPath path, boolean reload);

    TextureUV registerToAtlas(AssetPath path, TextureType type);

    GLTexture getTextureAtlas(TextureType type);

    GLTexture initTextureAtlas(TextureType type);
}
