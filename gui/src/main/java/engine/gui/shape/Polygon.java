package engine.gui.shape;

import com.github.mouse0w0.observable.collection.ObservableCollections;
import com.github.mouse0w0.observable.collection.ObservableList;
import engine.graphics.shape.Path2D;
import engine.gui.graphics.NodeRenderer;
import engine.gui.graphics.shape.PolygonRenderer;
import engine.gui.internal.shape.PolygonHelper;
import engine.gui.misc.Point;

import java.util.List;

public class Polygon extends Shape {

    private final ObservableList<Point> points = ObservableCollections.observableArrayList();

    private boolean geometryDirty;
    private float minX;
    private float minY;
    private float maxX;
    private float maxY;

    private Path2D path = Path2D.heap();

    static {
        PolygonHelper.setPolygonAccessor(Polygon::getPath2D);
    }

    public Polygon() {
        points.addChangeListener(change -> markDirty());
    }

    public Polygon(Point... points) {
        this();
        getPoints().addAll(points);
    }

    public ObservableList<Point> getPoints() {
        return points;
    }

    private Path2D getPath2D() {
        refreshGeometry();
        return path;
    }

    @Override
    public float prefWidth() {
        refreshGeometry();
        float width = maxX - minX;
        return width < 0 ? 0 : width;
    }

    @Override
    public float prefHeight() {
        refreshGeometry();
        float height = maxY - minY;
        return height < 0 ? 0 : height;
    }

    private void markDirty() {
        geometryDirty = true;
        requestParentLayout();
    }

    private void refreshGeometry() {
        if (!geometryDirty) return;
        geometryDirty = false;
        path.reset();
        if (points.isEmpty()) {
            minX = minY = maxX = maxY = 0;
            return;
        }

        List<Point> points = getPoints();
        Point point0 = points.get(0);
        minX = maxX = point0.getX();
        minY = maxY = point0.getY();
        path.moveTo(point0.getX(), point0.getY());
        for (int i = 1; i < points.size(); i++) {
            Point point = points.get(i);
            if (point.getX() < minX) minX = point.getX();
            else if (point.getX() > maxX) maxX = point.getX();
            if (point.getY() < minY) minY = point.getY();
            else if (point.getY() > maxY) maxY = point.getY();
            path.lineTo(point.getX(), point.getY());
        }
        path.closePath();
    }

    @Override
    protected NodeRenderer createDefaultRenderer() {
        return PolygonRenderer.INSTANCE;
    }
}
