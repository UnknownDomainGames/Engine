package nullengine.client.gui.layout;

import com.github.mouse0w0.observable.value.MutableFloatValue;
import com.github.mouse0w0.observable.value.MutableObjectValue;
import com.github.mouse0w0.observable.value.SimpleMutableFloatValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import nullengine.client.gui.Component;
import nullengine.client.gui.misc.Insets;
import nullengine.client.gui.misc.Pos;
import nullengine.client.gui.util.Utils;

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
        for (Component component : getChildren()) {
            width = Math.max(Math.max(width, component.width().get()), Utils.prefWidth(component));
        }
        Insets padding = padding().getValue();
        return padding.getLeft() + width + padding.getRight();
    }

    @Override
    public float computeHeight() {
        float height = 0, spacing = spacing().get();
        for (Component component : getChildren()) {
            height += Math.max(component.height().get(), Utils.prefHeight(component));
        }
        Insets padding = padding().getValue();
        return padding.getTop() + height + ((getChildren().size() == 0) ? 0 : spacing * (getChildren().size() - 1)) + padding.getBottom();
    }

    @Override
    protected void layoutChildren() {
        Insets padding = padding().getValue();
        float x, y = padding.getTop(), spacing = spacing().get(), w = width().get() - padding.getLeft() - padding.getRight();
        for (Component component : getChildren()) {
            float prefWidth = Utils.prefWidth(component);
            float prefHeight = Utils.prefHeight(component);
            x = alignment.getValue() == Pos.HPos.RIGHT ? w - prefWidth : alignment.getValue() == Pos.HPos.CENTER ? (w - prefWidth) / 2 : 0;
            x += padding.getLeft();
            layoutInArea(component, snap(x, true), snap(y, true), prefWidth, prefHeight);
            y += prefHeight + spacing;
        }
    }
}
