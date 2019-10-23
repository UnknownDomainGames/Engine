package nullengine.client.rendering.layer;

import nullengine.client.rendering.RenderManagerImpl;
import nullengine.client.rendering.queue.GeometryList;

public interface RenderLayerHandler {

    void render(RenderManagerImpl renderManager, GeometryList geometries);
}