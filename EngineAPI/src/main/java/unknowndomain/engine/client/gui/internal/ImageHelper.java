package unknowndomain.engine.client.gui.internal;

import unknowndomain.engine.client.asset.AssetPath;
import unknowndomain.engine.client.rendering.texture.GLTexture;

public interface ImageHelper {

    GLTexture getTexture(AssetPath path);
}
