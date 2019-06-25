package nullengine.client.gui.internal;

import nullengine.client.asset.AssetPath;
import nullengine.client.rendering.texture.GLTexture;

public interface ImageHelper {

    GLTexture getTexture(AssetPath path);
}
