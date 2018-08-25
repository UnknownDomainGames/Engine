package unknowndomain.engine.event.registry;

import unknowndomain.engine.GameContext;
import unknowndomain.engine.client.resource.ResourceManager;
import unknowndomain.engine.event.Event;

public class ResourceSetupEvent implements Event {
    private GameContext context;
    private ResourceManager resourceManager;

    public ResourceSetupEvent(GameContext context, ResourceManager resourceManager) {
        this.context = context;
        this.resourceManager = resourceManager;
    }

    public GameContext getContext() {
        return context;
    }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }
}
