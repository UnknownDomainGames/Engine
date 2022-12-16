package engine.gui.layout;

import com.github.mouse0w0.observable.value.*;
import engine.gui.Node;
import engine.gui.Parent;
import engine.gui.misc.Insets;
import engine.gui.misc.VPos;

public class HBox extends Pane {

    private MutableDoubleValue spacing;
    private MutableObjectValue<VPos> alignment;
    private MutableBooleanValue fillHeight;

    public final MutableDoubleValue spacing() {
        if (spacing == null) {
            spacing = new SimpleMutableDoubleValue();
            spacing.addChangeListener((observable, oldValue, newValue) -> needsLayout());
        }
        return spacing;
    }

    public final double getSpacing() {
        return spacing == null ? 0 : spacing.get();
    }

    public void setSpacing(double spacing) {
        spacing().set(spacing);
    }

    public final MutableObjectValue<VPos> alignment() {
        if (alignment == null) {
            alignment = new NonNullMutableObjectValue<>(VPos.TOP);
            alignment.addChangeListener((observable, oldValue, newValue) -> needsLayout());
        }
        return alignment;
    }

    public final VPos getAlignment() {
        return alignment == null ? VPos.TOP : alignment.get();
    }

    public final void setAlignment(VPos pos) {
        alignment().set(pos);
    }

    public final MutableBooleanValue fillHeight() {
        if (fillHeight == null) {
            fillHeight = new SimpleMutableBooleanValue();
        }
        return fillHeight;
    }

    public final boolean isFillHeight() {
        return fillHeight != null && fillHeight.get();
    }

    public final void setFillHeight(boolean fillHeight) {
        fillHeight().set(fillHeight);
    }

    @Override
    public double computeWidth() {
        double width = 0, spacing = spacing().get();
        for (Node node : getChildren()) {
            width += Math.max(node.getWidth(), Parent.prefWidth(node));
        }
        Insets padding = getPadding();
        return padding.getLeft() + width + ((getChildren().size() == 0) ? 0 : spacing * (getChildren().size() - 1)) + padding.getRight();
    }

    @Override
    public double computeHeight() {
        double height = 0;
        for (Node node : getChildren()) {
            height = Math.max(Math.max(height, node.getHeight()), Parent.prefHeight(node));
        }
        Insets padding = getPadding();
        return padding.getTop() + height + padding.getBottom();
    }

    @Override
    protected void layoutChildren() {
        Insets padding = getPadding();
        double x = padding.getLeft();
        double top = padding.getTop();
        double spacing = getSpacing();
        double contentHeight = getHeight() - padding.getTop() - padding.getBottom();
        boolean fillHeight = isFillHeight();
        for (Node node : getChildren()) {
            VPos alignment = getAlignment();
            double prefWidth = Parent.prefWidth(node);
            double prefHeight = fillHeight ? contentHeight : Parent.prefHeight(node);
            double y = alignment == VPos.BOTTOM ?
                    contentHeight - prefHeight : alignment == VPos.CENTER ? (contentHeight - prefHeight) / 2 : 0;
            layoutInArea(node, snap(x, true), snap(top + y, true), prefWidth, prefHeight);
            x += prefWidth + spacing;
        }
    }
}
