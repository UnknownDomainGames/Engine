package nullengine.client.event.rendering;

import nullengine.client.rendering.entity.EntityRenderManager;
import nullengine.client.rendering.entity.EntityRenderer;
import nullengine.entity.Entity;
import nullengine.event.Event;

public class EntityRendererRegistrationEvent implements Event {

    private final EntityRenderManager entityRenderManager;

    public EntityRendererRegistrationEvent(EntityRenderManager entityRenderManager) {
        this.entityRenderManager = entityRenderManager;
    }

    public <T extends Entity> void register(Class<T> entityType, EntityRenderer<T> renderer) {
        entityRenderManager.register(entityType, renderer);
    }
}
