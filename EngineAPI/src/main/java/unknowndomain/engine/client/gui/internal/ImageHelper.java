package unknowndomain.engine.client.gui.internal;

import unknowndomain.engine.client.rendering.texture.GLTexture;
import unknowndomain.engine.client.resource.ResourcePath;

public interface ImageHelper {

    GLTexture getTexture(ResourcePath path);
}
