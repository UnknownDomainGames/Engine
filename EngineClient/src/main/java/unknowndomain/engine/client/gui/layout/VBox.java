package unknowndomain.engine.client.gui.layout;

import com.github.mouse0w0.lib4j.observable.value.MutableFloatValue;
import com.github.mouse0w0.lib4j.observable.value.SimpleMutableFloatValue;
import unknowndomain.engine.client.gui.Component;

public class VBox extends Pane {

    private final MutableFloatValue spacing = new SimpleMutableFloatValue();

    public MutableFloatValue spacing() {
        return spacing;
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
            layoutInArea(component, 0, y, component.prefWidth(), component.prefHeight());
            y += component.prefHeight() + spacing;
        }
    }
}
