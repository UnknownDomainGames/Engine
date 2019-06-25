package nullengine.client.rendering.entity;

import nullengine.client.rendering.RenderContext;
import nullengine.entity.Entity;

public interface EntityRenderer<T extends Entity> {

    void init(RenderContext context);

    boolean shouldRender(T entity, double x, double y, double z, float partial);

    void render(T entity, double x, double y, double z, float partial);

    void dispose();
}
