package nullengine.client.gui.shape;

import java.util.Arrays;

public class Path extends Shape {

    private float[] points;
    private int length;

    public float[] getPoints() {
        return points;
    }

    public int getLength() {
        return length;
    }

    public void moveTo(float x, float y) {
        points = new float[10];
        points[0] = x;
        points[1] = y;
        length = 1;
    }

    public void lineTo(float x, float y) {
        if (points.length <= length * 2) {
            float[] np = Arrays.copyOf(points, points.length + 10);
            points = np;
        }
        points[length * 2] = x;
        points[length * 2 + 1] = y;
        length++;
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
        if (points.length <= length * 2) {
            float[] np = Arrays.copyOf(points, points.length + 10);
            points = np;
        }
        points[length * 2] = points[0];
        points[length * 2 + 1] = points[1];
        length++;
    }
}
