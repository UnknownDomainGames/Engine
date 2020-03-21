package engine.gui.layout;

import com.github.mouse0w0.observable.value.MutableFloatValue;
import com.github.mouse0w0.observable.value.MutableObjectValue;
import com.github.mouse0w0.observable.value.SimpleMutableFloatValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import engine.gui.Node;
import engine.gui.Parent;
import engine.gui.misc.HPos;
import engine.gui.misc.Insets;

public class VBox extends Pane {

    private final MutableFloatValue spacing = new SimpleMutableFloatValue();
    private final MutableObjectValue<HPos> alignment = new SimpleMutableObjectValue<>();

    public final MutableFloatValue spacing() {
        return spacing;
    }

    public final MutableObjectValue<HPos> alignment() {
        return alignment;
    }

    public VBox() {
        spacing.addChangeListener((observable, oldValue, newValue) -> needsLayout());
        alignment.addChangeListener((observable, oldValue, newValue) -> needsLayout());
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
        float x, y = padding.getTop(), spacing = spacing().get(), w = getWidth() - padding.getLeft() - padding.getRight();
        for (Node node : getChildren()) {
            float prefWidth = Parent.prefWidth(node);
            float prefHeight = Parent.prefHeight(node);
            x = alignment.get() == HPos.RIGHT ? w - prefWidth : alignment.get() == HPos.CENTER ? (w - prefWidth) / 2 : 0;
            x += padding.getLeft();
            layoutInArea(node, snap(x, true), snap(y, true), prefWidth, prefHeight);
            y += prefHeight + spacing;
        }
    }
}
