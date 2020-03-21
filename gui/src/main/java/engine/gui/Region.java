package engine.gui;

import com.github.mouse0w0.observable.value.MutableObjectValue;
import com.github.mouse0w0.observable.value.NonNullMutableObjectValue;
import engine.gui.misc.*;
import engine.gui.rendering.ComponentRenderer;
import engine.gui.rendering.RegionRenderer;
import engine.math.Math2;

public class Region extends Parent {

    public static final int USE_PARENT_VALUE = Size.USE_PARENT_VALUE;
    public static final int USE_COMPUTE_VALUE = Size.USE_COMPUTE_VALUE;

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

    public final float getMinWidth() {
        return size.getMinWidth();
    }

    public final float getMinHeight() {
        return size.getMinHeight();
    }

    public final void setMinSize(float width, float height) {
        size.setMinSize(width, height);
    }

    public final float getPrefWidth() {
        return size.getPrefWidth();
    }

    public final float getPrefHeight() {
        return size.getPrefHeight();
    }

    public final void setPrefSize(float width, float height) {
        size.setPrefSize(width, height);
    }

    public final float getMaxWidth() {
        return size.getMaxWidth();
    }

    public final float getMaxHeight() {
        return size.getMaxHeight();
    }

    public final void setMaxSize(float width, float height) {
        size.setMaxSize(width, height);
    }

    public static void positionInArea(Node child, float areaX, float areaY, float areaWidth, float areaHeight,
                                      float areaBaselineOffset, Insets margin, HPos halignment, VPos valignment, boolean isSnapToPixel) {
        Insets childMargin = margin != null ? margin : Insets.EMPTY;

        position(child, areaX, areaY, areaWidth, areaHeight, areaBaselineOffset,
                childMargin.getTop(),
                childMargin.getRight(),
                childMargin.getBottom(),
                childMargin.getLeft(),
                halignment, valignment, isSnapToPixel);
    }

    protected static float snap(float value, boolean snapToPixel) {
        return snapToPixel ? Math.round(value) : value;
    }

    private static void position(Node child, float areaX, float areaY, float areaWidth, float areaHeight,
                                 float areaBaselineOffset,
                                 float topMargin, float rightMargin, float bottomMargin, float leftMargin,
                                 HPos hpos, VPos vpos, boolean isSnapToPixel) {
        final float xoffset = leftMargin + computeXOffset(areaWidth - leftMargin - rightMargin,
                child.getWidth(), hpos);
        final float yoffset;
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
        final float x = snap(areaX + xoffset, isSnapToPixel);
        final float y = snap(areaY + yoffset, isSnapToPixel);

        child.relocate(x, y);
    }

    static float computeXOffset(float width, float contentWidth, HPos hpos) {
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

    static float computeYOffset(float height, float contentHeight, VPos vpos) {
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
    public float minWidth() {
        return getMinWidth();
    }

    @Override
    public float minHeight() {
        return getMinHeight();
    }

    @Override
    public final float prefWidth() {
        float width = getPrefWidth();
        if (width == Size.USE_COMPUTE_VALUE) {
            return computeWidth();
        } else if (width == Size.USE_PARENT_VALUE) {
            return (parent().isPresent() ? getParent().getWidth() : 0);
        }
        return width;
    }

    public float computeWidth() {
        return super.prefWidth();
    }

    @Override
    public final float prefHeight() {
        float height = getPrefHeight();
        if (height == Size.USE_COMPUTE_VALUE) {
            return computeHeight();
        } else if (height == Size.USE_PARENT_VALUE) {
            return (parent().isPresent() ? getParent().getHeight() : 0);
        }
        return height;
    }

    public float computeHeight() {
        return super.prefHeight();
    }

    @Override
    public float maxWidth() {
        return getMaxWidth();
    }

    @Override
    public float maxHeight() {
        return getMaxHeight();
    }

    @Override
    protected ComponentRenderer createDefaultRenderer() {
        return RegionRenderer.INSTANCE;
    }

    protected void layoutInArea(Node c, float areaX, float areaY, float areaWidth, float areaHeight, int areaBaselineOffset, Insets margin, HPos hAlign, VPos vAlign) {
        Insets childMargin = margin != null ? margin : Insets.EMPTY;

        float top = childMargin.getTop();
        float bottom = childMargin.getBottom();
        float left = childMargin.getLeft();
        float right = childMargin.getRight();

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

    protected float computeChildPrefAreaWidth(Node child, float baselineComplement, Insets margin, float height, boolean fillHeight) {
        float left = margin != null ? margin.getLeft() : 0;
        float right = margin != null ? margin.getRight() : 0;
        float alt = -1;
        if (height != -1) {
            //TODO width depends on height
        }
        return left + Math2.clamp(child.minWidth(), child.prefWidth(), child.maxWidth()) + right;
    }

    protected float computeChildPrefAreaHeight(Node child, float baselineComplement, Insets margin, float width) {
        float top = margin != null ? margin.getTop() : 0;
        float bottom = margin != null ? margin.getBottom() : 0;
        float alt = -1;
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
