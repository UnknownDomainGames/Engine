package nullengine.client.rendering.queue;

import nullengine.client.rendering.management.RenderManager;

public interface RenderTypeHandler {

    void render(RenderManager manager, GeometryList geometries);
}