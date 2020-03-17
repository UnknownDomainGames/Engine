package engine.graphics.shader;

import engine.graphics.texture.Sampler;
import engine.graphics.texture.Texture;

public interface TextureBinding {
    int getUnit();

    Texture getTexture();

    Sampler getSampler();

    void set(Texture texture);

    void set(Texture texture, Sampler sampler);
}
