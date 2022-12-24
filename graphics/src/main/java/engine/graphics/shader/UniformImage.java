package engine.graphics.shader;

import engine.graphics.texture.Texture;

public interface UniformImage extends Uniform {
    int getUnit();

    Texture getTexture();

    void setTexture(Texture texture);
}
