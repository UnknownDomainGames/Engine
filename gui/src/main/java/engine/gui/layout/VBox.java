package engine.gui.layout;

import com.github.mouse0w0.observable.value.*;
import engine.gui.Node;
import engine.gui.Parent;
import engine.gui.misc.HPos;
import engine.gui.misc.Insets;

public class VBox extends Pane {

    private MutableFloatValue spacing;
    private MutableObjectValue<HPos> alignment;
    private MutableBooleanValue fillWidth;

    public VBox() {
    }

    public final MutableFloatValue spacing() {
        if (spacing == null) {
            spacing = new SimpleMutableFloatValue();
            spacing.addChangeListener((observable, oldValue, newValue) -> needsLayout());
        }
        return spacing;
    }

    public final float getSpacing() {
        return spacing == null ? 0 : spacing.get();
    }

    public void setSpacing(float spacing) {
        spacing().set(spacing);
    }

    public final MutableObjectValue<HPos> alignment() {
        if (alignment == null) {
            alignment = new NonNullMutableObjectValue<>(HPos.LEFT);
            alignment.addChangeListener((observable, oldValue, newValue) -> needsLayout());
        }
        return alignment;
    }

    public final HPos getAlignment() {
        return alignment == null ? HPos.LEFT : alignment.get();
    }

    public final void setAlignment(HPos pos) {
        alignment().set(pos);
    }

    public final MutableBooleanValue fillWidth() {
        if (fillWidth == null) {
            fillWidth = new SimpleMutableBooleanValue();
        }
        return fillWidth;
    }

    public final boolean isFillWidth() {
        return fillWidth != null && fillWidth.get();
    }

    public final void setFillWidth(boolean fillWidth) {
        fillWidth().set(fillWidth);
    }

    @Override
    public float computeWidth() {
        float width = 0;
        for (Node node : getChildren()) {
            width = Math.max(Math.max(width, node.getWidth()), Parent.prefWidth(node));
        }
        Insets padding = getPadding();
        return padding.getLeft() + width + padding.getRight();
    }

    @Override
    public float computeHeight() {
        float height = 0, spacing = spacing().get();
        for (Node node : getChildren()) {
            height += Math.max(node.getHeight(), Parent.prefHeight(node));
        }
        Insets padding = getPadding();
        return padding.getTop() + height + ((getChildren().size() == 0) ? 0 : spacing * (getChildren().size() - 1)) + padding.getBottom();
    }

    @Override
    protected void layoutChildren() {
        Insets padding = getPadding();
        float left = padding.getLeft();
        float y = padding.getTop();
        float spacing = getSpacing();
        float contentWidth = getWidth() - padding.getLeft() - padding.getRight();
        boolean fillWidth = isFillWidth();
        for (Node node : getChildren()) {
            float prefWidth = fillWidth ? contentWidth : Parent.prefWidth(node);
            float prefHeight = Parent.prefHeight(node);
            float x = alignment.get() == HPos.RIGHT ? contentWidth - prefWidth : alignment.get() == HPos.CENTER ? (contentWidth - prefWidth) / 2 : 0;
            layoutInArea(node, snap(left + x, true), snap(y, true), prefWidth, prefHeight);
            y += prefHeight + spacing;
        }
    }
}
