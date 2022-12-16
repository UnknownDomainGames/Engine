package engine.gui.misc;

import java.util.Objects;

public class Insets {

    public static final Insets EMPTY = new Insets(0);

    private final double top;
    private final double right;
    private final double bottom;
    private final double left;

    public Insets(double topRightBottomLeft) {
        this(topRightBottomLeft, topRightBottomLeft, topRightBottomLeft, topRightBottomLeft);
    }

    public Insets(double top, double right, double bottom, double left) {
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
    }

    public double getTop() {
        return top;
    }

    public double getRight() {
        return right;
    }

    public double getBottom() {
        return bottom;
    }

    public double getLeft() {
        return left;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Insets insets = (Insets) o;
        return Double.compare(insets.top, top) == 0 &&
                Double.compare(insets.bottom, bottom) == 0 &&
                Double.compare(insets.left, left) == 0 &&
                Double.compare(insets.right, right) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(top, bottom, left, right);
    }

    @Override
    public String toString() {
        return "Insets{" +
                "top=" + top +
                ", bottom=" + bottom +
                ", left=" + left +
                ", right=" + right +
                '}';
    }
}
