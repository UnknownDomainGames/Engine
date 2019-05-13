package unknowndomain.engine.client.rendering.texture;

import unknowndomain.engine.registry.RegistryEntry;

/**
 * @see StandardTextureAtlas
 */
public final class TextureAtlasName extends RegistryEntry.Impl<TextureAtlasName> {

    public static TextureAtlasName of(String name) {
        return new TextureAtlasName().registerName(name);
    }
}
