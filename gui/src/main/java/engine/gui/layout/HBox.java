package engine.gui.layout;

import com.github.mouse0w0.observable.value.MutableFloatValue;
import com.github.mouse0w0.observable.value.SimpleMutableFloatValue;
import engine.gui.Node;
import engine.gui.Parent;
import engine.gui.misc.Insets;

public class HBox extends Pane {

    private final MutableFloatValue spacing = new SimpleMutableFloatValue();

    public final MutableFloatValue spacing() {
        return spacing;
    }

    @Override
    public float computeWidth() {
        float width = 0, spacing = spacing().get();
        for (Node node : getChildren()) {
            width += Math.max(node.getWidth(), Parent.prefWidth(node));
        }
        Insets padding = getPadding();
        return padding.getLeft() + width + ((getChildren().size() == 0) ? 0 : spacing * (getChildren().size() - 1)) + padding.getRight();
    }

    @Override
    public float computeHeight() {
        float height = 0;
        for (Node node : getChildren()) {
            height = Math.max(Math.max(height, node.getHeight()), Parent.prefHeight(node));
        }
        Insets padding = getPadding();
        return padding.getTop() + height + padding.getBottom();
    }

    @Override
    protected void layoutChildren() {
        Insets padding = getPadding();
        float x = padding.getLeft(), y = padding.getTop(), spacing = spacing().get();
        for (Node node : getChildren()) {
            float prefWidth = Parent.prefWidth(node);
            float prefHeight = Parent.prefHeight(node);
            layoutInArea(node, x, y, prefWidth, prefHeight);
            x += prefWidth + spacing;
        }
    }
}
