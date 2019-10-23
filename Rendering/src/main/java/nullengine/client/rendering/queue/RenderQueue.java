package nullengine.client.rendering.queue;

import nullengine.client.rendering.RenderManagerImpl;
import nullengine.client.rendering.layer.RenderLayer;
import nullengine.client.rendering.layer.RenderLayerHandler;
import nullengine.client.rendering.scene.Geometry;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class RenderQueue {

    private final Map<RenderLayer, GeometryList> queue = new HashMap<>();

    public void add(@Nonnull Geometry geometry, RenderLayer layer) {
        if (layer == null) return;
        queue.computeIfAbsent(layer, key -> new GeometryList()).add(geometry);
    }

    public void remove(Geometry geometry, RenderLayer layer) {
        if (layer == null) return;
        queue.computeIfAbsent(layer, key -> new GeometryList()).remove(geometry);
    }

    public void clear() {
        queue.clear();
    }

    public void render(RenderManagerImpl renderManager, RenderLayer layer, RenderLayerHandler handler) {
        GeometryList geometries = queue.get(layer);
        if (geometries == null) return;
        handler.render(renderManager, geometries);
    }
}
