package nullengine.client.rendering.gl.texture;

import nullengine.client.rendering.texture.Texture;

public interface GLTexture extends Texture {
    int getId();

    int getTarget();
}
