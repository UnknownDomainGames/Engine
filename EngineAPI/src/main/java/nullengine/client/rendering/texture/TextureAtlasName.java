package nullengine.client.rendering.texture;

import nullengine.registry.RegistryEntry;

/**
 * @see StandardTextureAtlas
 */
public final class TextureAtlasName extends RegistryEntry.Impl<TextureAtlasName> {

    public static TextureAtlasName of(String name) {
        return new TextureAtlasName().name(name);
    }
}
