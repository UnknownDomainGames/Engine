package nullengine.client.gui.layout;

import nullengine.client.gui.Component;
import nullengine.client.gui.misc.Insets;
import nullengine.client.gui.util.Utils;

public class AnchorPane extends Pane {
    public static final String TOP_ANCHOR = "anchor-top";
    public static final String BOTTOM_ANCHOR = "anchor-bottom";
    public static final String LEFT_ANCHOR = "anchor-left";
    public static final String RIGHT_ANCHOR = "anchor-right";

    public static void setTopAnchor(Component child, Float value) {
        setProperty(child, TOP_ANCHOR, value);
    }

    public static void setBottomAnchor(Component child, Float value) {
        setProperty(child, BOTTOM_ANCHOR, value);
    }

    public static void setLeftAnchor(Component child, Float value) {
        setProperty(child, LEFT_ANCHOR, value);
    }

    public static void setRightAnchor(Component child, Float value) {
        setProperty(child, RIGHT_ANCHOR, value);
    }

    public static Float getTopAnchor(Component child) {
        return (Float) getProperty(child, TOP_ANCHOR);
    }

    public static Float getBottomAnchor(Component child) {
        return (Float) getProperty(child, BOTTOM_ANCHOR);
    }

    public static Float getLeftAnchor(Component child) {
        return (Float) getProperty(child, LEFT_ANCHOR);
    }

    public static Float getRightAnchor(Component child) {
        return (Float) getProperty(child, RIGHT_ANCHOR);
    }

    @Override
    public float prefWidth() {
        return computeWidth(false);
    }

    @Override
    public float prefHeight() {
        return computeHeight(false);
    }

    private float computeWidth(final boolean minimum) {
        float max = 0;
        for (Component child : getChildren()) {
            var leftA = getLeftAnchor(child);
            var rightA = getRightAnchor(child);
            float left = leftA != null ? leftA : (0);
            float right = rightA != null ? rightA : 0;
            float childH = -1;
            max = Math.max(max, left + (minimum && leftA != null && rightA != null ? child.minWidth() : computeChildPrefAreaWidth(child, -1, null, childH, false)) + right);
        }
        final Insets padding = padding().getValue();
        return padding.getLeft() + max + padding.getRight();
    }

    private float computeHeight(final boolean minimum) {
        float max = 0;
        for (Component child : getChildren()) {
            var topA = getTopAnchor(child);
            var bottomA = getBottomAnchor(child);
            float top = topA != null ? topA : (0);
            float bottom = bottomA != null ? bottomA : 0;
            float childW = -1;
            max = Math.max(max, top + (minimum && topA != null && bottomA != null ? child.minHeight() : computeChildPrefAreaHeight(child, -1, null, childW)) + bottom);
        }
        final Insets padding = padding().getValue();
        return padding.getTop() + max + padding.getBottom();
    }

    private float computeChildWidth(Component component, Float left, Float right, float areaWidth) {
        if (left != null && right != null) {
            return areaWidth - left - right;
        }
        return Utils.prefWidth(component);
    }

    private float computeChildHeight(Component component, Float top, Float bottom, float areaHeight) {
        if (top != null && bottom != null) {
            return areaHeight - top - bottom;
        }
        return Utils.prefHeight(component);
    }

    @Override
    protected void layoutChildren() {
        final var children = getChildren();
        for (Component child : children) {
            var top = getTopAnchor(child);
            var bottom = getBottomAnchor(child);
            var left = getLeftAnchor(child);
            var right = getRightAnchor(child);


            float x = 0, y = 0;
            float w = computeChildWidth(child, left, right, width().get());
            float h = computeChildHeight(child, top, bottom, height().get());
            if (left != null) {
                x = left;
            } else if (right != null) {
                x = width().get() - right - w;
            }
            if (top != null) {
                y = top;
            } else if (bottom != null) {
                y = height().get() - bottom - h;
            }

            layoutInArea(child, x, y, w, h);
        }
    }
}
