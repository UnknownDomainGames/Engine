package engine.gui.shape;

import engine.gui.graphics.ComponentRenderer;
import org.joml.Vector2f;
import org.joml.Vector2fc;

import java.util.ArrayList;
import java.util.List;

public class Path extends Shape {

    public static abstract class Point {
    }

    public static class LinearPoint extends Point {
        private Vector2fc coord;

        public LinearPoint(float x, float y) {
            coord = new Vector2f(x, y);
        }

        public Vector2fc getCoordinate() {
            return coord;
        }
    }


    private List<Point> points = new ArrayList<>();

    public List<Point> getPoints() {
        return points;
    }

    public int getLength() {
        return points.size();
    }

    public void moveTo(float x, float y) {
        points.clear();
        points.add(new LinearPoint(x, y));
    }

    public void lineTo(float x, float y) {
        points.add(new LinearPoint(x, y));
    }

    //TODO: we should store the following commands for GraphicsImpl, except close()!

    public void quadTo() {
        //TODO: quad. bezier line: Qx1 y1, x,y, where (x1,y1) is control point
    }

    public void curveTo() {
        //TODO: curve: Cx1 y1, x2 y2, x y, where (x1,y1) and (x2,y2) are control points
    }

    public void arcTo() {
        //TODO: Arx ry x-axis-rotation large-arc-flag sweep-flag x y
    }

    public void close() {
        points.add(points.get(0));
    }

    @Override
    public float prefWidth() {
        return 0;
    }

    @Override
    public float prefHeight() {
        return 0;
    }

    @Override
    protected ComponentRenderer createDefaultRenderer() {
        return null;
    }
}
