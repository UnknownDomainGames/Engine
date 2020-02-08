package engine.gui.layout;

import com.github.mouse0w0.observable.value.MutableFloatValue;
import com.github.mouse0w0.observable.value.SimpleMutableFloatValue;
import engine.gui.Node;
import engine.gui.misc.Insets;
import engine.gui.util.Utils;

public class HBox extends Pane {

    private final MutableFloatValue spacing = new SimpleMutableFloatValue();

    public final MutableFloatValue spacing() {
        return spacing;
    }

    @Override
    public float computeWidth() {
        float width = 0, spacing = spacing().get();
        for (Node node : getChildren()) {
            width += Math.max(node.width().get(), Utils.prefWidth(node));
        }
        Insets padding = padding().getValue();
        return padding.getLeft() + width + ((getChildren().size() == 0) ? 0 : spacing * (getChildren().size() - 1)) + padding.getRight();
    }

    @Override
    public float computeHeight() {
        float height = 0;
        for (Node node : getChildren()) {
            height = Math.max(Math.max(height, node.height().get()), Utils.prefHeight(node));
        }
        Insets padding = padding().getValue();
        return padding.getTop() + height + padding.getBottom();
    }

    @Override
    protected void layoutChildren() {
        Insets padding = padding().getValue();
        float x = padding.getLeft(), y = padding.getTop(), spacing = spacing().get();
        for (Node node : getChildren()) {
            float prefWidth = Utils.prefWidth(node);
            float prefHeight = Utils.prefHeight(node);
            layoutInArea(node, x, y, prefWidth, prefHeight);
            x += prefWidth + spacing;
        }
    }
}
