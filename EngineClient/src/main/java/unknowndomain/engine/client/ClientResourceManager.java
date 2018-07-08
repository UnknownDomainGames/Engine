package unknowndomain.engine.client;

import unknowndomain.engine.client.resource.Texture2D;
import unknowndomain.engine.registry.ResourceManagerDefault;

/**
 * Lists of resources manager in client side
 */
public class ClientResourceManager {
    public static final ResourceManagerDefault<Texture2D> TEXTURE_MANAGER = new ResourceManagerDefault<>();
}
