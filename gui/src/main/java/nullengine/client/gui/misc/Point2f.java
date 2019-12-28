package nullengine.client.gui.misc;

import java.util.Objects;

public class Point2f {

    private final float x;
    private final float y;

    public Point2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point2f point2f = (Point2f) o;
        return Float.compare(point2f.x, x) == 0 &&
                Float.compare(point2f.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Point2f{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
