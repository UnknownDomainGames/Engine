package engine.graphics.shader;

import engine.graphics.texture.Sampler;
import engine.graphics.texture.Texture;

public interface UniformTexture {
    String getName();

    int getUnit();

    Texture getTexture();

    void setTexture(Texture texture);

    Sampler getSampler();

    void setSampler(Sampler sampler);
}
