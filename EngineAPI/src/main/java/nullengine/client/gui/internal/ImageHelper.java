package nullengine.client.gui.internal;

import nullengine.client.asset.AssetURL;
import nullengine.client.rendering.texture.GLTexture;

public interface ImageHelper {

    GLTexture getTexture(AssetURL path);
}
