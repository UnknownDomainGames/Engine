package engine.graphics.shader;

import engine.graphics.texture.Texture;

public interface UniformImage {
    String getName();

    ImageBinding getBinding();

    int getUnit();

    Texture getTexture();

    void set(Texture texture);
}
