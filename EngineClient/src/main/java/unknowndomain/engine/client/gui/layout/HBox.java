package unknowndomain.engine.client.gui.layout;

import com.github.mouse0w0.lib4j.observable.value.MutableFloatValue;
import com.github.mouse0w0.lib4j.observable.value.SimpleMutableFloatValue;
import unknowndomain.engine.client.gui.Component;

public class HBox extends Pane {

    private final MutableFloatValue spacing = new SimpleMutableFloatValue();

    public final MutableFloatValue spacing() {
        return spacing;
    }

    @Override
    public float prefWidth() {
        float width = 0, spacing = spacing().get();
        for (Component component : getChildren()) {
            width += component.prefWidth();
        }
        return width + getChildren().size() == 0 ? 0 : spacing * (getChildren().size() - 1);
    }

    @Override
    public float prefHeight() {
        float height = 0;
        for (Component component : getChildren()) {
            height = Math.max(height, component.prefHeight());
        }
        return height;
    }

    @Override
    protected void layoutChildren() {
        float x = 0, spacing = spacing().get();
        for (Component component : getChildren()) {
            float prefWidth = component.prefWidth();
            float prefHeight = component.prefHeight();
            layoutInArea(component, x, 0, prefWidth, prefHeight);
            x += prefWidth + spacing;
        }
    }
}
