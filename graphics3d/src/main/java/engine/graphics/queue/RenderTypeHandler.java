package engine.graphics.queue;

import engine.graphics.backend.GraphicsBackend;

public interface RenderTypeHandler {

    void render(GraphicsBackend manager, GeometryList geometries);
}