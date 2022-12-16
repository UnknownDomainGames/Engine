package engine.gui;

import com.github.mouse0w0.observable.value.MutableObjectValue;
import com.github.mouse0w0.observable.value.NonNullMutableObjectValue;
import engine.gui.graphics.NodeRenderer;
import engine.gui.graphics.RegionRenderer;
import engine.gui.misc.*;
import engine.math.Math2;

public class Region extends Parent {
    public static final double USE_COMPUTED_VALUE = Size.USE_COMPUTED_VALUE;
    public static final double USE_PERF_VALUE = Size.USE_PERF_VALUE;

    private MutableObjectValue<Background> background;
    private MutableObjectValue<Border> border;
    private MutableObjectValue<Insets> padding;

    private final Size size = new Size();

    public final MutableObjectValue<Background> background() {
        if (background == null) {
            background = new NonNullMutableObjectValue<>(Background.NOTHING);
        }
        return background;
    }

    public final Background getBackground() {
        return background == null ? Background.NOTHING : background.get();
    }

    public final void setBackground(Background background) {
        background().set(background);
    }

    public final MutableObjectValue<Border> border() {
        if (border == null) {
            border = new NonNullMutableObjectValue<>(Border.NO_BORDER);
        }
        return border;
    }

    public final Border getBorder() {
        return border == null ? Border.NO_BORDER : border.get();
    }

    public final void setBorder(Border border) {
        border().set(border);
    }

    public final MutableObjectValue<Insets> padding() {
        if (padding == null) {
            padding = new NonNullMutableObjectValue<>(Insets.EMPTY);
        }
        return padding;
    }

    public final Insets getPadding() {
        return padding == null ? Insets.EMPTY : padding.get();
    }

    public final void setPadding(Insets padding) {
        padding().set(padding);
    }

    public final Size getSize() {
        return size;
    }

    public double getMinWidth() {
        return size.getMinWidth();
    }

    public void setMinWidth(double minWidth) {
        size.setMinWidth(minWidth);
    }

    public double getMinHeight() {
        return size.getMinHeight();
    }

    public void setMinHeight(double minHeight) {
        size.setMinHeight(minHeight);
    }

    public void setMinSize(double width, double height) {
        size.setMinSize(width, height);
    }

    public double getPrefWidth() {
        return size.getPrefWidth();
    }

    public void setPrefWidth(double prefWidth) {
        size.setPrefWidth(prefWidth);
    }

    public double getPrefHeight() {
        return size.getPrefHeight();
    }

    public void setPrefHeight(double prefHeight) {
        size.setPrefHeight(prefHeight);
    }

    public void setPrefSize(double width, double height) {
        size.setPrefSize(width, height);
    }

    public double getMaxWidth() {
        return size.getMaxWidth();
    }

    public void setMaxWidth(double maxWidth) {
        size.setMaxWidth(maxWidth);
    }

    public double getMaxHeight() {
        return size.getMaxHeight();
    }

    public void setMaxHeight(double maxHeight) {
        size.setMaxHeight(maxHeight);
    }

    public void setMaxSize(double width, double height) {
        size.setMaxSize(width, height);
    }

    public static void positionInArea(Node child, double areaX, double areaY, double areaWidth, double areaHeight,
                                      double areaBaselineOffset, Insets margin, HPos halignment, VPos valignment, boolean isSnapToPixel) {
        Insets childMargin = margin != null ? margin : Insets.EMPTY;

        position(child, areaX, areaY, areaWidth, areaHeight, areaBaselineOffset,
                childMargin.getTop(),
                childMargin.getRight(),
                childMargin.getBottom(),
                childMargin.getLeft(),
                halignment, valignment, isSnapToPixel);
    }

    protected static double snap(double value, boolean snapToPixel) {
        return snapToPixel ? Math.round(value) : value;
    }

    private static void position(Node child, double areaX, double areaY, double areaWidth, double areaHeight,
                                 double areaBaselineOffset,
                                 double topMargin, double rightMargin, double bottomMargin, double leftMargin,
                                 HPos hpos, VPos vpos, boolean isSnapToPixel) {
        final double xoffset = leftMargin + computeXOffset(areaWidth - leftMargin - rightMargin,
                child.getWidth(), hpos);
        final double yoffset;
        /*if (vpos == Pos.VPos.BASELINE) {
            double bo = child.getBaselineOffset();
            if (bo == BASELINE_OFFSET_SAME_AS_HEIGHT) {
                // We already know the layout bounds at this stage, so we can use them
                yoffset = areaBaselineOffset - child.getLayoutBounds().getHeight();
            } else {
                yoffset = areaBaselineOffset - bo;
            }
        } else */
        {
            yoffset = topMargin + computeYOffset(areaHeight - topMargin - bottomMargin,
                    child.getHeight(), vpos);
        }
        final double x = snap(areaX + xoffset, isSnapToPixel);
        final double y = snap(areaY + yoffset, isSnapToPixel);

        child.relocate(x, y);
    }

    static double computeXOffset(double width, double contentWidth, HPos hpos) {
        switch (hpos) {
            case LEFT:
                return 0;
            case CENTER:
                return (width - contentWidth) / 2;
            case RIGHT:
                return width - contentWidth;
            default:
                throw new AssertionError("Unhandled hPos");
        }
    }

    static double computeYOffset(double height, double contentHeight, VPos vpos) {
        switch (vpos) {
            case BASELINE:
            case TOP:
                return 0;
            case CENTER:
                return (height - contentHeight) / 2;
            case BOTTOM:
                return height - contentHeight;
            default:
                throw new AssertionError("Unhandled vPos");
        }
    }

    @Override
    public double minWidth() {
        double minWidth = getMinWidth();
        if (minWidth == USE_COMPUTED_VALUE) {
            minWidth = super.minWidth();
        } else if (minWidth == USE_PERF_VALUE) {
            minWidth = prefWidth();
        }
        return minWidth < 0 ? 0 : minWidth;
    }

    @Override
    public double minHeight() {
        double minHeight = getMinHeight();
        if (minHeight == USE_COMPUTED_VALUE) {
            minHeight = super.minHeight();
        } else if (minHeight == USE_PERF_VALUE) {
            minHeight = prefHeight();
        }
        return minHeight < 0 ? 0 : minHeight;
    }

    @Override
    public final double prefWidth() {
        double width = getPrefWidth();
        if (width == USE_COMPUTED_VALUE) {
            return computeWidth();
        }
        return width;
    }

    public double computeWidth() {
        return super.prefWidth();
    }

    @Override
    public final double prefHeight() {
        double height = getPrefHeight();
        if (height == USE_COMPUTED_VALUE) {
            return computeHeight();
        }
        return height;
    }

    public double computeHeight() {
        return super.prefHeight();
    }

    @Override
    public double maxWidth() {
        double maxWidth = getMaxWidth();
        if (maxWidth == USE_COMPUTED_VALUE) {
            maxWidth = super.maxWidth();
        } else if (maxWidth == USE_PERF_VALUE) {
            maxWidth = prefWidth();
        }
        return maxWidth < 0 ? 0 : maxWidth;
    }

    @Override
    public double maxHeight() {
        double maxHeight = getMaxHeight();
        if (maxHeight == USE_COMPUTED_VALUE) {
            maxHeight = super.maxHeight();
        } else if (maxHeight == USE_PERF_VALUE) {
            maxHeight = prefHeight();
        }
        return maxHeight < 0 ? 0 : maxHeight;
    }

    @Override
    protected void layoutChildren() {
        Insets padding = getPadding();
        double x = padding.getLeft();
        double y = padding.getTop();
        double w = getWidth() - x - padding.getRight();
        double h = getHeight() - y - padding.getBottom();
        layoutChildren(x, y, w, h);
    }

    protected void layoutChildren(double contentX, double contentY, double contentWidth, double contentHeight) {
        for (Node node : getChildren()) {
            layoutInArea(node, contentX + node.getLayoutX(), contentY + node.getLayoutY(), prefWidth(node), prefHeight(node));
        }
    }

    @Override
    protected NodeRenderer createDefaultRenderer() {
        return RegionRenderer.INSTANCE;
    }

    protected void layoutInArea(Node c, double areaX, double areaY, double areaWidth, double areaHeight, int areaBaselineOffset, Insets margin, HPos hAlign, VPos vAlign) {
        Insets childMargin = margin != null ? margin : Insets.EMPTY;

        double top = childMargin.getTop();
        double bottom = childMargin.getBottom();
        double left = childMargin.getLeft();
        double right = childMargin.getRight();

//        if (valignment == VPos.BASELINE) {
//            double bo = child.getBaselineOffset();
//            if (bo == BASELINE_OFFSET_SAME_AS_HEIGHT) {
//                if (child.isResizable()) {
//                    // Everything below the baseline is like an "inset". The Node with BASELINE_OFFSET_SAME_AS_HEIGHT cannot
//                    // be resized to this area
//                    bottom += snapSpace(areaHeight - areaBaselineOffset, isSnapToPixel);
//                } else {
//                    top = snapSpace(areaBaselineOffset - child.getLayoutBounds().getHeight(), isSnapToPixel);
//                }
//            } else {
//                top = snapSpace(areaBaselineOffset - bo, isSnapToPixel);
//            }
//        }


//        if (child.isResizable()) {
//            Vec2d size = boundedNodeSizeWithBias(child, areaWidth - left - right, areaHeight - top - bottom,
//                    fillWidth, fillHeight, TEMP_VEC2D);
//        }
        c.resize(areaWidth - left - right, areaHeight - top - bottom);
        position(c, areaX, areaY, areaWidth, areaHeight, areaBaselineOffset,
                top, right, bottom, left, hAlign, vAlign, true);
    }

    protected double computeChildPrefAreaWidth(Node child, double baselineComplement, Insets margin, double height, boolean fillHeight) {
        double left = margin != null ? margin.getLeft() : 0;
        double right = margin != null ? margin.getRight() : 0;
        double alt = -1;
        if (height != -1) {
            //TODO width depends on height
        }
        return left + Math2.clamp(child.minWidth(), child.prefWidth(), child.maxWidth()) + right;
    }

    protected double computeChildPrefAreaHeight(Node child, double baselineComplement, Insets margin, double width) {
        double top = margin != null ? margin.getTop() : 0;
        double bottom = margin != null ? margin.getBottom() : 0;
        double alt = -1;
        if (false) {
            //TODO height depends on width
        }

        if (baselineComplement != -1) {
            return 0; //TODO
        } else {
            return top + Math2.clamp(child.minHeight(), child.prefHeight(), child.maxHeight()) + bottom;
        }
    }
}
