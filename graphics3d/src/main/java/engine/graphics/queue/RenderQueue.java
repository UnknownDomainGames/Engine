package engine.graphics.queue;

import engine.graphics.Geometry;
import engine.util.Validate;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RenderQueue {

    private final Map<RenderType, GeometryList> queue = Collections.synchronizedMap(new HashMap<>());

    public GeometryList getGeometryList(RenderType type) {
        return queue.computeIfAbsent(type, key -> new GeometryList());
    }

    public void add(@Nonnull Geometry geometry, RenderType type) {
        Validate.notNull(geometry);
        if (type == null) return;
        getGeometryList(type).add(geometry);
    }

    public void remove(Geometry geometry, RenderType type) {
        if (type == null) return;
        getGeometryList(type).remove(geometry);
    }

    public void clear() {
        queue.clear();
    }
}
