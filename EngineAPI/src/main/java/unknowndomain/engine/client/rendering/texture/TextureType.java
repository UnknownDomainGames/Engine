package unknowndomain.engine.client.rendering.texture;

/**
 * @see TextureTypes
 */
public final class TextureType {

    public static TextureType of(String name) {
        return new TextureType(name);
    }

    private final String name;

    private TextureType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "TextureType{" +
                "name='" + name + '\'' +
                '}';
    }
}
