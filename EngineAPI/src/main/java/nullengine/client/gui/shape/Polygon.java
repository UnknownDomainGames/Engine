package nullengine.client.gui.shape;

import java.util.Arrays;

public class Polygon extends Shape {

    private final float[] points;

    public Polygon(float... points) {
        this.points = points;
    }

    public float[] getPoints() {
        return points;
    }

    public static Polygon fromPath(Path path) {
        return new Polygon(Arrays.copyOf(path.getPoints(), path.getLength() * 2));
    }
}
