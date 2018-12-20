package unknowndomain.engine.client.rendering.texture;

import unknowndomain.engine.client.resource.ResourcePath;

public interface TextureManager {

    TextureUV register(ResourcePath path, TextureType type);

    GLTexture getTextureAtlas(TextureType type);

    GLTexture initTextureAtlas(TextureType type);
}
