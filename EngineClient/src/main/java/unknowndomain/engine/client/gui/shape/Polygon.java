package unknowndomain.engine.client.gui.shape;

public class Polygon extends Shape {

    private final float[] points;

    public Polygon(float... points) {
        this.points = points;
    }

    public float[] getPoints() {
        return points;
    }
}
