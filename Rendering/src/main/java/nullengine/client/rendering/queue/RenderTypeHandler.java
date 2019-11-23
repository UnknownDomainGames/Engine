package nullengine.client.rendering.queue;

import nullengine.client.rendering.management.RenderContext;

public interface RenderTypeHandler {

    void render(RenderContext manager, GeometryList geometries);
}