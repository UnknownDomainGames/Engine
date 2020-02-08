package engine.gui.layout;

import com.github.mouse0w0.observable.value.MutableFloatValue;
import com.github.mouse0w0.observable.value.MutableObjectValue;
import com.github.mouse0w0.observable.value.SimpleMutableFloatValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import engine.gui.Node;
import engine.gui.misc.Insets;
import engine.gui.misc.Pos;
import engine.gui.util.Utils;

public class VBox extends Pane {

    private final MutableFloatValue spacing = new SimpleMutableFloatValue();
    private final MutableObjectValue<Pos.HPos> alignment = new SimpleMutableObjectValue<>();

    public final MutableFloatValue spacing() {
        return spacing;
    }

    public final MutableObjectValue<Pos.HPos> alignment() {
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
            width = Math.max(Math.max(width, node.width().get()), Utils.prefWidth(node));
        }
        Insets padding = padding().getValue();
        return padding.getLeft() + width + padding.getRight();
    }

    @Override
    public float computeHeight() {
        float height = 0, spacing = spacing().get();
        for (Node node : getChildren()) {
            height += Math.max(node.height().get(), Utils.prefHeight(node));
        }
        Insets padding = padding().getValue();
        return padding.getTop() + height + ((getChildren().size() == 0) ? 0 : spacing * (getChildren().size() - 1)) + padding.getBottom();
    }

    @Override
    protected void layoutChildren() {
        Insets padding = padding().getValue();
        float x, y = padding.getTop(), spacing = spacing().get(), w = width().get() - padding.getLeft() - padding.getRight();
        for (Node node : getChildren()) {
            float prefWidth = Utils.prefWidth(node);
            float prefHeight = Utils.prefHeight(node);
            x = alignment.getValue() == Pos.HPos.RIGHT ? w - prefWidth : alignment.getValue() == Pos.HPos.CENTER ? (w - prefWidth) / 2 : 0;
            x += padding.getLeft();
            layoutInArea(node, snap(x, true), snap(y, true), prefWidth, prefHeight);
            y += prefHeight + spacing;
        }
    }
}
