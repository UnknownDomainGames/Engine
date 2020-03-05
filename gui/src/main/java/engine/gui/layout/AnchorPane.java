package engine.gui.layout;

import engine.gui.Node;
import engine.gui.misc.Insets;
import engine.gui.util.Utils;

public class AnchorPane extends Pane {
    public static final String TOP_ANCHOR = "anchor-top";
    public static final String BOTTOM_ANCHOR = "anchor-bottom";
    public static final String LEFT_ANCHOR = "anchor-left";
    public static final String RIGHT_ANCHOR = "anchor-right";

    public static void setTopAnchor(Node child, Float value) {
        setProperty(child, TOP_ANCHOR, value);
    }

    public static void setBottomAnchor(Node child, Float value) {
        setProperty(child, BOTTOM_ANCHOR, value);
    }

    public static void setLeftAnchor(Node child, Float value) {
        setProperty(child, LEFT_ANCHOR, value);
    }

    public static void setRightAnchor(Node child, Float value) {
        setProperty(child, RIGHT_ANCHOR, value);
    }

    public static Float getTopAnchor(Node child) {
        return (Float) getProperty(child, TOP_ANCHOR);
    }

    public static Float getBottomAnchor(Node child) {
        return (Float) getProperty(child, BOTTOM_ANCHOR);
    }

    public static Float getLeftAnchor(Node child) {
        return (Float) getProperty(child, LEFT_ANCHOR);
    }

    public static Float getRightAnchor(Node child) {
        return (Float) getProperty(child, RIGHT_ANCHOR);
    }

    @Override
    public float computeWidth() {
        return computeWidth(false);
    }

    @Override
    public float computeHeight() {
        return computeHeight(false);
    }

    private float computeWidth(final boolean minimum) {
        float max = 0;
        for (Node child : getChildren()) {
            var leftA = getLeftAnchor(child);
            var rightA = getRightAnchor(child);
            float left = leftA != null ? leftA : (0);
            float right = rightA != null ? rightA : 0;
            float childH = -1;
            max = Math.max(max, left + (minimum && leftA != null && rightA != null ? child.minWidth() : computeChildPrefAreaWidth(child, -1, null, childH, false)) + right);
        }
        final Insets padding = padding().get();
        return padding.getLeft() + max + padding.getRight();
    }

    private float computeHeight(final boolean minimum) {
        float max = 0;
        for (Node child : getChildren()) {
            var topA = getTopAnchor(child);
            var bottomA = getBottomAnchor(child);
            float top = topA != null ? topA : (0);
            float bottom = bottomA != null ? bottomA : 0;
            float childW = -1;
            max = Math.max(max, top + (minimum && topA != null && bottomA != null ? child.minHeight() : computeChildPrefAreaHeight(child, -1, null, childW)) + bottom);
        }
        final Insets padding = padding().get();
        return padding.getTop() + max + padding.getBottom();
    }

    private float computeChildWidth(Node node, Float left, Float right, float areaWidth) {
        if (left != null && right != null) {
            return areaWidth - left - right;
        }
        return Utils.prefWidth(node);
    }

    private float computeChildHeight(Node node, Float top, Float bottom, float areaHeight) {
        if (top != null && bottom != null) {
            return areaHeight - top - bottom;
        }
        return Utils.prefHeight(node);
    }

    @Override
    protected void layoutChildren() {
        final var children = getChildren();
        for (Node child : children) {
            var top = getTopAnchor(child);
            var bottom = getBottomAnchor(child);
            var left = getLeftAnchor(child);
            var right = getRightAnchor(child);


            float x = 0, y = 0;
            float w = computeChildWidth(child, left, right, getWidth());
            float h = computeChildHeight(child, top, bottom, getHeight());
            if (left != null) {
                x = left;
            } else if (right != null) {
                x = getWidth() - right - w;
            }
            if (top != null) {
                y = top;
            } else if (bottom != null) {
                y = getHeight() - bottom - h;
            }

            layoutInArea(child, x, y, w, h);
        }
    }
}
