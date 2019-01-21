package unknowndomain.engine.client.gui.layout;

import com.github.mouse0w0.lib4j.observable.value.MutableFloatValue;
import com.github.mouse0w0.lib4j.observable.value.SimpleMutableFloatValue;
import unknowndomain.engine.client.gui.Component;

public class VBox extends Pane {

    private final MutableFloatValue spacing = new SimpleMutableFloatValue();

    public final MutableFloatValue spacing() {
        return spacing;
    }

    public VBox() {
        spacing.addChangeListener((observable, oldValue, newValue) -> needsLayout());
    }

    @Override
    public float prefWidth() {
        float width = 0;
        for (Component component : getChildren()) {
            width = Math.max(width, component.prefWidth());
        }
        return width;
    }

    @Override
    public float prefHeight() {
        float y = 0, spacing = spacing().get();
        for (Component component : getChildren()) {
            y += component.prefHeight();
        }
        return y + getChildren().size() == 0 ? 0 : spacing * (getChildren().size() - 1);
    }

    @Override
    protected void layoutChildren() {
        float y = 0, spacing = spacing().get();
        for (Component component : getChildren()) {
            float prefWidth = component.prefWidth();
            float prefHeight = component.prefHeight();
            layoutInArea(component, 0, y, prefWidth, prefHeight);
            y += prefHeight + spacing;
        }
    }
}
