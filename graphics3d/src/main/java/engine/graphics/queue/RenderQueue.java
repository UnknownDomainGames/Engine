package engine.graphics.queue;

import engine.graphics.Geometry;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class RenderQueue {

    private final Map<RenderType, GeometryList> queue = new HashMap<>();

    public GeometryList getGeometryList(RenderType type) {
        return queue.get(type);
    }

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
}
