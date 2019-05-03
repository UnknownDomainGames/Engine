package unknowndomain.engine.client.rendering.texture;

import unknowndomain.engine.registry.RegistryEntry;

/**
 * @see TextureTypes
 */
public final class TextureType extends RegistryEntry.Impl<TextureType> {

    public static TextureType of(String name) {
        return new TextureType().registerName(name);
    }
}
