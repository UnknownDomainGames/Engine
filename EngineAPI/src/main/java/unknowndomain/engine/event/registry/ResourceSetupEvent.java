package unknowndomain.engine.event.registry;

import unknowndomain.engine.client.rendering.texture.TextureManager;
import unknowndomain.engine.client.resource.ResourceManager;
import unknowndomain.engine.event.Event;
import unknowndomain.engine.game.GameContext;

/**
 * 
 * @Deprecated Replaced by EngineEvent.ResourceConstructionStart
 */
@Deprecated
public class ResourceSetupEvent implements Event {

    private final GameContext context;
    private final ResourceManager resourceManager;
    private final TextureManager textureManager;

    public ResourceSetupEvent(GameContext context, ResourceManager resourceManager, TextureManager textureManager) {
        this.context = context;
        this.resourceManager = resourceManager;
        this.textureManager = textureManager;
    }

    public GameContext getContext() {
        return context;
    }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    public TextureManager getTextureManager() {
        return textureManager;
    }
}
