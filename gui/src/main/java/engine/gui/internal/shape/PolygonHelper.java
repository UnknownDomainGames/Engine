package engine.gui.internal.shape;

import engine.graphics.shape.Path2D;
import engine.gui.shape.Polygon;

public abstract class PolygonHelper {

    private static PolygonAccessor polygonAccessor;

    public static PolygonAccessor getPolygonAccessor() {
        return polygonAccessor;
    }

    public static void setPolygonAccessor(PolygonAccessor accessor) {
        if (polygonAccessor != null) {
            throw new IllegalStateException("Cannot set twice");
        }
        polygonAccessor = accessor;
    }

    public static Path2D getPath2D(Polygon polygon) {
        return polygonAccessor.getPath2D(polygon);
    }

    public interface PolygonAccessor {
        Path2D getPath2D(Polygon polygon);
    }
}
