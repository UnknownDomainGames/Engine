package com.github.unknownstudio.unknowndomain.engine.client;

import com.github.unknownstudio.unknowndomain.engine.client.resource.Texture2D;
import com.github.unknownstudio.unknowndomain.engine.registry.ResourceManagerDefault;

/**
 * Lists of resources manager in client side
 */
public class ResourcesManagersClient {
    public static final ResourceManagerDefault<Texture2D> TEXTURE_MANAGER = new ResourceManagerDefault<>();
}
