package nullengine.client.gui.layout;

import com.github.mouse0w0.observable.value.MutableFloatValue;
import com.github.mouse0w0.observable.value.SimpleMutableFloatValue;
import nullengine.client.gui.Component;
import nullengine.client.gui.misc.Insets;
import nullengine.client.gui.util.Utils;

public class HBox extends Pane {

    private final MutableFloatValue spacing = new SimpleMutableFloatValue();

    public final MutableFloatValue spacing() {
        return spacing;
    }

    @Override
    public float prefWidth() {
        float width = 0, spacing = spacing().get();
        for (Component component : getChildren()) {
            width += Math.max(component.width().get(), Utils.prefWidth(component));
        }
        Insets padding = padding().getValue();
        return padding.getLeft() + width + ((getChildren().size() == 0) ? 0 : spacing * (getChildren().size() - 1)) + padding.getRight();
    }

    @Override
    public float prefHeight() {
        float height = 0;
        for (Component component : getChildren()) {
            height = Math.max(Math.max(height, component.height().get()),Utils.prefHeight(component));
        }
        Insets padding = padding().getValue();
        return padding.getTop() + height + padding.getBottom();
    }

    @Override
    protected void layoutChildren() {
        Insets padding = padding().getValue();
        float x = padding.getLeft(), y = padding.getTop(), spacing = spacing().get();
        for (Component component : getChildren()) {
            float prefWidth = Utils.prefWidth(component);
            float prefHeight = Utils.prefHeight(component);
            layoutInArea(component, x, y, prefWidth, prefHeight);
            x += prefWidth + spacing;
        }
    }
}
