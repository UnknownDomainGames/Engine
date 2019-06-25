package nullengine.client.gui.layout;

import com.github.mouse0w0.lib4j.observable.value.MutableValue;
import com.github.mouse0w0.lib4j.observable.value.SimpleMutableObjectValue;
import nullengine.client.gui.Component;
import nullengine.client.gui.misc.Insets;
import nullengine.client.gui.misc.Pos;

public class BorderPane extends Pane {

    private static final String MARGIN = "borderpane-margin";
    private static final String ALIGNMENT = "borderpane-alignment";

    public static void setAlignment(Component component, Pos pos) {
        Pane.setProperty(component, ALIGNMENT, pos);
    }

    public static Pos getAlignment(Component component) {
        return (Pos) getProperty(component, ALIGNMENT);
    }

    public static void setMargin(Component child, Insets value) {
        setProperty(child, MARGIN, value);
    }

    /**
     * Returns the child's margin constraint if set.
     *
     * @param child the child node of a border pane
     * @return the margin for the child or null if no margin was set
     */
    public static Insets getMargin(Component child) {
        return (Insets) getProperty(child, MARGIN);
    }

    // convenience for handling null margins
    private static Insets getNodeMargin(Component child) {
        Insets margin = getMargin(child);
        return margin != null ? margin : Insets.EMPTY;
    }

    private final MutableValue<Component> center = new SimpleMutableObjectValue<>();
    private final MutableValue<Component> top = new SimpleMutableObjectValue<>();
    private final MutableValue<Component> bottom = new SimpleMutableObjectValue<>();
    private final MutableValue<Component> left = new SimpleMutableObjectValue<>();
    private final MutableValue<Component> right = new SimpleMutableObjectValue<>();

    public MutableValue<Component> center() {
        return center;
    }

    public MutableValue<Component> top() {
        return top;
    }

    public MutableValue<Component> bottom() {
        return bottom;
    }

    public MutableValue<Component> left() {
        return left;
    }

    public MutableValue<Component> right() {
        return right;
    }

    public BorderPane() {
        center.addChangeListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                getChildren().remove(oldValue);
            }
            if (newValue != null) {
                setAlignment(newValue, Pos.CENTER);
                getChildren().add(newValue);
            }
        });
        left.addChangeListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                getChildren().remove(oldValue);
            }
            if (newValue != null) {
                setAlignment(newValue, Pos.CENTER_LEFT);
                getChildren().add(newValue);
            }
        });
        right.addChangeListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                getChildren().remove(oldValue);
            }
            if (newValue != null) {
                setAlignment(newValue, Pos.CENTER_RIGHT);
                getChildren().add(newValue);
            }
        });
        top.addChangeListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                getChildren().remove(oldValue);
            }
            if (newValue != null) {
                setAlignment(newValue, Pos.TOP_CENTER);
                getChildren().add(newValue);
            }
        });
        bottom.addChangeListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                getChildren().remove(oldValue);
            }
            if (newValue != null) {
                setAlignment(newValue, Pos.BOTTOM_CENTER);
                getChildren().add(newValue);
            }
        });
    }

    @Override
    public float prefWidth() {
        var padding = padding().getValue();
        float width = 0f;
        if (padding != null) {
            width += padding.getLeft() + padding.getRight();
        }
        if (left.getValue() != null) {
            width += left.getValue().prefWidth();
        }
        if (center.getValue() != null) {
            width += center.getValue().prefWidth();
        }
        if (right.getValue() != null) {
            width += right.getValue().prefWidth();
        }
        return width;
    }

    @Override
    public float prefHeight() {
        var padding = padding().getValue();
        float height = 0f;
        if (padding != null) {
            height += padding.getTop() + padding.getBottom();
        }
        if (top.getValue() != null) {
            height += top.getValue().prefHeight();
        }
        if (center.getValue() != null) {
            height += center.getValue().prefHeight();
        }
        if (bottom.getValue() != null) {
            height += bottom.getValue().prefHeight();
        }
        return height;
    }

    @Override
    protected void layoutChildren() {
        final Insets padding = padding().getValue();
        float width = width().get();
        float height = height().get();
        width = width < minWidth() ? minWidth() : width;
        height = height < minHeight() ? minHeight() : height;


        final float insideX = padding.getLeft();
        final float insideY = padding.getTop();
        final float insideWidth = width - insideX - padding.getRight();
        final float insideHeight = height - insideY - padding.getBottom();
        final Component c = center().getValue();
        final Component r = right().getValue();
        final Component b = bottom().getValue();
        final Component l = left().getValue();
        final Component t = top().getValue();
        float topHeight = 0;
        if (t != null) {
            Insets topMargin = getNodeMargin(t);
            //double adjustedWidth = adjustWidthByMargin(insideWidth, topMargin);
            //double adjustedHeight = adjustHeightByMargin(insideHeight, topMargin);
            topHeight = t.prefHeight();
//            Vec2d result = boundedNodeSizeWithBias(t, adjustedWidth,
//                    topHeight, true, true, TEMP_VEC2D);
//            topHeight = snapSize(result.y);
            t.resize(t.prefWidth(), topHeight);

//            topHeight = snapSpace(topMargin.getBottom()) + topHeight + snapSpace(topMargin.getTop());
            Pos alignment = getAlignment(t);
            positionInArea(t, insideX, insideY, insideWidth, topHeight, 0/*ignore baseline*/,
                    topMargin,
                    alignment != null ? alignment.getHpos() : Pos.HPos.LEFT,
                    alignment != null ? alignment.getVpos() : Pos.VPos.TOP, false);
        }

        float bottomHeight = 0;
        if (b != null) {
            Insets bottomMargin = getNodeMargin(b);
//            double adjustedWidth = adjustWidthByMargin(insideWidth, bottomMargin);
//            double adjustedHeight = adjustHeightByMargin(insideHeight - topHeight, bottomMargin);
            bottomHeight = b.prefHeight();
//            bottomHeight = Math.min(bottomHeight, adjustedHeight);
//            Vec2d result = boundedNodeSizeWithBias(b, adjustedWidth,
//                    bottomHeight, true, true, TEMP_VEC2D);
//            bottomHeight = snapSize(result.y);
            b.resize(b.prefWidth(), bottomHeight);

//            bottomHeight = snapSpace(bottomMargin.getBottom()) + bottomHeight + snapSpace(bottomMargin.getTop());
            Pos alignment = getAlignment(b);
            positionInArea(b, insideX, insideY + insideHeight - bottomHeight,
                    insideWidth, bottomHeight, 0/*ignore baseline*/,
                    bottomMargin,
                    alignment != null ? alignment.getHpos() : Pos.HPos.LEFT,
                    alignment != null ? alignment.getVpos() : Pos.VPos.BOTTOM, false);
        }

        float leftWidth = 0;
        if (l != null) {
            Insets leftMargin = getNodeMargin(l);
//            double adjustedWidth = adjustWidthByMargin(insideWidth, leftMargin);
//            double adjustedHeight = adjustHeightByMargin(insideHeight - topHeight - bottomHeight, leftMargin); // ????
            leftWidth = l.prefWidth();
//            leftWidth = Math.min(leftWidth, adjustedWidth);
//            Vec2d result = boundedNodeSizeWithBias(l, leftWidth, adjustedHeight,
//                    true, true, TEMP_VEC2D);
//            leftWidth = snapSize(result.x);
            l.resize(leftWidth, l.prefHeight());

//            leftWidth = snapSpace(leftMargin.getLeft()) + leftWidth + snapSpace(leftMargin.getRight());
            Pos alignment = getAlignment(l);
            positionInArea(l, insideX, insideY + topHeight,
                    leftWidth, insideHeight - topHeight - bottomHeight, 0/*ignore baseline*/,
                    leftMargin,
                    alignment != null ? alignment.getHpos() : Pos.HPos.LEFT,
                    alignment != null ? alignment.getVpos() : Pos.VPos.TOP, false);
        }
        float rightWidth = 0;
        if (r != null) {
            Insets rightMargin = getNodeMargin(r);
//            double adjustedWidth = adjustWidthByMargin(insideWidth - leftWidth, rightMargin);
//            double adjustedHeight = adjustHeightByMargin(insideHeight - topHeight - bottomHeight, rightMargin);

            rightWidth = r.prefWidth();
//            rightWidth = Math.min(rightWidth, adjustedWidth);
//            Vec2d result = boundedNodeSizeWithBias(r, rightWidth, adjustedHeight,
//                    true, true, TEMP_VEC2D);
//            rightWidth = snapSize(result.x);
            r.resize(rightWidth, r.prefHeight());

//            rightWidth = snapSpace(rightMargin.getLeft()) + rightWidth + snapSpace(rightMargin.getRight());
            Pos alignment = getAlignment(r);
            positionInArea(r, insideX + insideWidth - rightWidth, insideY + topHeight,
                    rightWidth, insideHeight - topHeight - bottomHeight, 0/*ignore baseline*/,
                    rightMargin,
                    alignment != null ? alignment.getHpos() : Pos.HPos.RIGHT,
                    alignment != null ? alignment.getVpos() : Pos.VPos.TOP, false);
        }

        if (c != null) {
            Pos alignment = getAlignment(c);

            layoutInArea(c, insideX + leftWidth, insideY + topHeight,
                    insideWidth - leftWidth - rightWidth,
                    insideHeight - topHeight - bottomHeight, 0/*ignore baseline*/,
                    getNodeMargin(c),
                    alignment != null ? alignment.getHpos() : Pos.HPos.CENTER,
                    alignment != null ? alignment.getVpos() : Pos.VPos.CENTER);
        }
    }

}
