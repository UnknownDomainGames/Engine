package nullengine.client.gui.misc;

import java.util.Objects;

public class Insets {

    public static final Insets EMPTY = new Insets(0);

    private final float top;
    private final float right;
    private final float bottom;
    private final float left;

    public Insets(float topRightBottomLeft) {
        this(topRightBottomLeft, topRightBottomLeft, topRightBottomLeft, topRightBottomLeft);
    }

    public Insets(float top, float right, float bottom, float left) {
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
    }

    public float getTop() {
        return top;
    }

    public float getRight() {
        return right;
    }

    public float getBottom() {
        return bottom;
    }

    public float getLeft() {
        return left;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Insets insets = (Insets) o;
        return Float.compare(insets.top, top) == 0 &&
                Float.compare(insets.bottom, bottom) == 0 &&
                Float.compare(insets.left, left) == 0 &&
                Float.compare(insets.right, right) == 0;
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
