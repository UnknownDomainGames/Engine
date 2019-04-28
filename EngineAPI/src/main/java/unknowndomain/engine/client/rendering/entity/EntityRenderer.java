package unknowndomain.engine.client.rendering.entity;

import unknowndomain.engine.client.rendering.RenderContext;
import unknowndomain.engine.entity.Entity;

public interface EntityRenderer<T extends Entity> {

    void init(RenderContext renderContext);

    boolean shouldRender(T entity, double x, double y, double z, float partial);

    void render(T entity, double x, double y, double z, float partial);

    void dispose();
}
