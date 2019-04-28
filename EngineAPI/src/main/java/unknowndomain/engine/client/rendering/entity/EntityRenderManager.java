package unknowndomain.engine.client.rendering.entity;

import unknowndomain.engine.entity.Entity;

public interface EntityRenderManager {
    <T extends Entity> void register(Class<T> entityType, EntityRenderer<T> renderer);

    void render(Entity entity, float partial);
}
