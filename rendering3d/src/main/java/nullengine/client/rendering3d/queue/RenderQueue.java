package nullengine.client.rendering3d.queue;

import nullengine.client.rendering.management.RenderManager;
import nullengine.client.rendering3d.Geometry;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class RenderQueue {

    private final Map<RenderType, GeometryList> queue = new HashMap<>();

    public void add(@Nonnull Geometry geometry, RenderType layer) {
        if (layer == null) return;
        queue.computeIfAbsent(layer, key -> new GeometryList()).add(geometry);
    }

    public void remove(Geometry geometry, RenderType layer) {
        if (layer == null) return;
        queue.computeIfAbsent(layer, key -> new GeometryList()).remove(geometry);
    }

    public void clear() {
        queue.clear();
    }

    public void render(RenderManager manager, RenderType layer, RenderTypeHandler handler) {
        GeometryList geometries = queue.get(layer);
        if (geometries == null) return;
        handler.render(manager, geometries);
    }
}
