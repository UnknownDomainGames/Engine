package unknowndomain.engine.client.event.rendering;

import unknowndomain.engine.client.rendering.entity.EntityRenderManager;
import unknowndomain.engine.client.rendering.entity.EntityRenderer;
import unknowndomain.engine.entity.Entity;
import unknowndomain.engine.event.Event;

public class EntityRendererRegistrationEvent implements Event {

    private final EntityRenderManager entityRenderManager;

    public EntityRendererRegistrationEvent(EntityRenderManager entityRenderManager) {
        this.entityRenderManager = entityRenderManager;
    }

    public <T extends Entity> void register(Class<T> entityType, EntityRenderer<T> renderer) {
        entityRenderManager.register(entityType, renderer);
    }
}
