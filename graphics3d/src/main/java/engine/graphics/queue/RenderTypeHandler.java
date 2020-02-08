package engine.graphics.queue;

import engine.graphics.management.GraphicsBackend;

public interface RenderTypeHandler {

    void render(GraphicsBackend manager, GeometryList geometries);
}