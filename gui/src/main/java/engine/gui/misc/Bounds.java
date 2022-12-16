package engine.gui.misc;

import java.util.Objects;

public final class Bounds {
    public static final Bounds EMPTY = new Bounds(0, 0, 0, 0);

    private final double minX;
    private final double minY;
    private final double maxX;
    private final double maxY;
    private final double width;
    private final double height;

    public Bounds(double minX, double minY, double width, double height) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = minX + width;
        this.maxY = minY + height;
        this.width = width;
        this.height = height;
    }

    public double getMinX() {
        return minX;
    }

    public double getMinY() {
        return minY;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMaxY() {
        return maxY;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public boolean isEmpty() {
        return minX >= maxX || minY >= maxY;
    }

    public boolean contains(Point point) {
        return contains(point.getX(), point.getY());
    }

    public boolean contains(double x, double y) {
        return x >= minX && x <= maxX && y >= minY && y <= maxY;
    }

    public boolean contains(Bounds bounds) {
        return contains(bounds.getMinX(), bounds.getMinY()) && contains(bounds.getMaxX(), bounds.getMaxY());
    }

    public boolean intersects(double x, double y, double width, double height) {
        if (isEmpty() || width < 0 || height < 0) return false;
        return x + width >= minX && y + height >= minY && x < maxX && y < maxY;
    }

    public boolean intersects(Bounds bounds) {
        if (bounds == null || bounds.isEmpty()) return false;
        return intersects(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bounds bounds = (Bounds) o;
        return Double.compare(bounds.minX, minX) == 0 &&
                Double.compare(bounds.minY, minY) == 0 &&
                Double.compare(bounds.width, width) == 0 &&
                Double.compare(bounds.height, height) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(minX, minY, width, height);
    }

    @Override
    public String toString() {
        return "Bounds{" +
                "minX=" + minX +
                ", minY=" + minY +
                ", maxX=" + maxX +
                ", maxY=" + maxY +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
