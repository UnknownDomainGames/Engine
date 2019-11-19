package nullengine.client.rendering.graphics;

import nullengine.client.rendering.management.RenderContext;
import nullengine.client.rendering.queue.GeometryList;

public interface RenderTypeHandler {

    void render(RenderContext manager, GeometryList geometries);
}