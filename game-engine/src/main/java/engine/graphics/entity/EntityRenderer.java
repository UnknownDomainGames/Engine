package engine.graphics.entity;

import engine.entity.Entity;
import engine.graphics.RenderManager;

public interface EntityRenderer<T extends Entity> {

    void init(RenderManager context);

    boolean shouldRender(T entity, double x, double y, double z, float partial);

    void render(T entity, double x, double y, double z, float partial);

    void dispose();
}
