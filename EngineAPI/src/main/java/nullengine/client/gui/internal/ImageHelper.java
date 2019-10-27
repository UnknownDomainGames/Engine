package nullengine.client.gui.internal;

import nullengine.client.asset.AssetURL;
import nullengine.client.rendering.texture.Texture2D;

public interface ImageHelper {

    Texture2D getTexture(AssetURL path);
}
