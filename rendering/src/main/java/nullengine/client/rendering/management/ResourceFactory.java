package nullengine.client.rendering.management;

import nullengine.client.rendering.texture.Texture2D;

public interface ResourceFactory {

    Texture2D.Builder createTexture2DBuilder();
}
