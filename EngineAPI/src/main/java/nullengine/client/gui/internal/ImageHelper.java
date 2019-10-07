package nullengine.client.gui.internal;

import nullengine.client.asset.AssetURL;
import nullengine.client.rendering.texture.Texture;

public interface ImageHelper {

    Texture getTexture(AssetURL path);
}
