package engine.graphics.shader;

import engine.graphics.texture.Texture;

public interface UniformImage {
    String getName();

    int getUnit();

    Texture getTexture();

    void setTexture(Texture texture);
}
