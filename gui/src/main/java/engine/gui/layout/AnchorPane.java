package engine.gui.layout;

import engine.gui.Node;
import engine.gui.Parent;
import engine.gui.misc.Insets;

public class AnchorPane extends Pane {
    public static final String TOP_ANCHOR = "anchor-top";
    public static final String BOTTOM_ANCHOR = "anchor-bottom";
    public static final String LEFT_ANCHOR = "anchor-left";
    public static final String RIGHT_ANCHOR = "anchor-right";

    public static void setTopAnchor(Node child, Double value) {
        setProperty(child, TOP_ANCHOR, value);
    }

    public static void setBottomAnchor(Node child, Double value) {
        setProperty(child, BOTTOM_ANCHOR, value);
    }

    public static void setLeftAnchor(Node child, Double value) {
        setProperty(child, LEFT_ANCHOR, value);
    }

    public static void setRightAnchor(Node child, Double value) {
        setProperty(child, RIGHT_ANCHOR, value);
    }

    public static Double getTopAnchor(Node child) {
        return (Double) getProperty(child, TOP_ANCHOR);
    }

    public static Double getBottomAnchor(Node child) {
        return (Double) getProperty(child, BOTTOM_ANCHOR);
    }

    public static Double getLeftAnchor(Node child) {
        return (Double) getProperty(child, LEFT_ANCHOR);
    }

    public static Double getRightAnchor(Node child) {
        return (Double) getProperty(child, RIGHT_ANCHOR);
    }

    @Override
    public double computeWidth() {
        return computeWidth(false);
    }

    @Override
    public double computeHeight() {
        return computeHeight(false);
    }

    private double computeWidth(final boolean minimum) {
        double max = 0;
        for (Node child : getChildren()) {
            var leftA = getLeftAnchor(child);
            var rightA = getRightAnchor(child);
            double left = leftA != null ? leftA : (0);
            double right = rightA != null ? rightA : 0;
            double childH = -1;
            max = Math.max(max, left + (minimum && leftA != null && rightA != null ? child.minWidth() : computeChildPrefAreaWidth(child, -1, null, childH, false)) + right);
        }
        final Insets padding = getPadding();
        return padding.getLeft() + max + padding.getRight();
    }

    private double computeHeight(final boolean minimum) {
        double max = 0;
        for (Node child : getChildren()) {
            var topA = getTopAnchor(child);
            var bottomA = getBottomAnchor(child);
            double top = topA != null ? topA : (0);
            double bottom = bottomA != null ? bottomA : 0;
            double childW = -1;
            max = Math.max(max, top + (minimum && topA != null && bottomA != null ? child.minHeight() : computeChildPrefAreaHeight(child, -1, null, childW)) + bottom);
        }
        final Insets padding = getPadding();
        return padding.getTop() + max + padding.getBottom();
    }

    private double computeChildWidth(Node node, Double left, Double right, double areaWidth) {
        if (left != null && right != null) {
            return areaWidth - left - right;
        }
        return Parent.prefWidth(node);
    }

    private double computeChildHeight(Node node, Double top, Double bottom, double areaHeight) {
        if (top != null && bottom != null) {
            return areaHeight - top - bottom;
        }
        return Parent.prefHeight(node);
    }

    @Override
    protected void layoutChildren() {
        final var children = getChildren();
        for (Node child : children) {
            var top = getTopAnchor(child);
            var bottom = getBottomAnchor(child);
            var left = getLeftAnchor(child);
            var right = getRightAnchor(child);


            double x = 0, y = 0;
            double w = computeChildWidth(child, left, right, getWidth());
            double h = computeChildHeight(child, top, bottom, getHeight());
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
