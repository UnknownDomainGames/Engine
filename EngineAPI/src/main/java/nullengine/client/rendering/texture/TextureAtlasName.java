package nullengine.client.rendering.texture;

import java.util.Objects;

/**
 * @see StandardTextureAtlas
 */
public final class TextureAtlasName {

    private final String name;

    public static TextureAtlasName of(String name) {
        return new TextureAtlasName(name);
    }

    public TextureAtlasName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextureAtlasName that = (TextureAtlasName) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
