package unknowndomain.engine.client.rendering.texture;

import unknowndomain.engine.client.resource.ResourcePath;

import javax.annotation.Nullable;

public interface TextureManager {

    @Nullable
    GLTexture register(ResourcePath path);

    GLTexture getTexture(ResourcePath path);

    TextureUV registerToAtlas(ResourcePath path, TextureType type);

    GLTexture getTextureAtlas(TextureType type);

    GLTexture initTextureAtlas(TextureType type);
}
