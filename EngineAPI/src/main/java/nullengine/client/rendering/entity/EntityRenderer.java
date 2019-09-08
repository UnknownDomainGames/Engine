package nullengine.client.rendering.entity;

import nullengine.client.rendering.RenderManager;
import nullengine.entity.Entity;

public interface EntityRenderer<T extends Entity> {

    void init(RenderManager context);

    boolean shouldRender(T entity, double x, double y, double z, float partial);

    void render(T entity, double x, double y, double z, float partial);

    void dispose();
}
