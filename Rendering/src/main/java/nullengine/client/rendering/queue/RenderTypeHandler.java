package nullengine.client.rendering.queue;

import nullengine.client.rendering.management.RenderingContext;

public interface RenderTypeHandler {

    void render(RenderingContext manager, GeometryList geometries);
}