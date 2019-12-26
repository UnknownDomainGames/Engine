package nullengine.client.rendering.scene.queue;

import nullengine.client.rendering.management.RenderManager;

public interface RenderTypeHandler {

    void render(RenderManager manager, GeometryList geometries);
}