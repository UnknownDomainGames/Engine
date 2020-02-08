package engine.graphics.queue;

import engine.graphics.management.RenderManager;

public interface RenderTypeHandler {

    void render(RenderManager manager, GeometryList geometries);
}