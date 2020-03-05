package engine.gui.misc;

import java.util.Objects;

public final class Bounds {

    private final float minX;
    private final float minY;
    private final float maxX;
    private final float maxY;
    private final float width;
    private final float height;

    public Bounds(float minX, float minY, float width, float height) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = minX + width;
        this.maxY = minY + height;
        this.width = width;
        this.height = height;
    }

    public float getMinX() {
        return minX;
    }

    public float getMinY() {
        return minY;
    }

    public float getMaxX() {
        return maxX;
    }

    public float getMaxY() {
        return maxY;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public boolean isEmpty() {
        return minX > maxX || minY > maxY;
    }

    public boolean contains(Point point) {
        return contains(point.getX(), point.getY());
    }

    public boolean contains(float x, float y) {
        return x >= minX && x <= maxX && y >= minY && y <= maxY;
    }

    public boolean contains(Bounds bounds) {
        return contains(bounds.getMinX(), bounds.getMinY()) && contains(bounds.getMaxX(), bounds.getMaxY());
    }

    public boolean intersects(float x, float y, float width, float height) {
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
        return Float.compare(bounds.minX, minX) == 0 &&
                Float.compare(bounds.minY, minY) == 0 &&
                Float.compare(bounds.width, width) == 0 &&
                Float.compare(bounds.height, height) == 0;
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
