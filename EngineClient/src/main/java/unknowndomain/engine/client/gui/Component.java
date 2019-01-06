package unknowndomain.engine.client.gui;

import com.github.mouse0w0.lib4j.observable.collection.ObservableCollections;
import com.github.mouse0w0.lib4j.observable.collection.ObservableMap;
import com.github.mouse0w0.lib4j.observable.value.*;
import unknowndomain.engine.client.gui.rendering.ComponentRenderer;

import java.util.HashMap;

public abstract class Component {

    final MutableValue<Scene> scene = new SimpleMutableObjectValue<>();
    final MutableValue<Container> parent = new SimpleMutableObjectValue<>();

    private final MutableFloatValue x = new SimpleMutableFloatValue();
    private final MutableFloatValue y = new SimpleMutableFloatValue();
    final MutableFloatValue width = new SimpleMutableFloatValue();
    final MutableFloatValue height = new SimpleMutableFloatValue();

    private final MutableBooleanValue visible = new SimpleMutableBooleanValue(true);
    private final MutableBooleanValue disabled = new SimpleMutableBooleanValue(false);

    final MutableBooleanValue focused = new SimpleMutableBooleanValue(false);
    final MutableBooleanValue hover = new SimpleMutableBooleanValue(false);
    final MutableBooleanValue pressed = new SimpleMutableBooleanValue(false);

    private ComponentRenderer renderer;

    public final ObservableValue<Container> parent() {
        return parent.toImmutable();
    }

    public final MutableFloatValue x() {
        return x;
    }

    public final MutableFloatValue y() {
        return y;
    }

    public final ObservableFloatValue width() {
        return width.toImmutable();
    }

    public final ObservableFloatValue height() {
        return height.toImmutable();
    }

    public final MutableBooleanValue visible() {
        return visible;
    }

    public final MutableBooleanValue disabled() {
        return disabled;
    }

    public final ObservableBooleanValue focused() {
        return focused.toImmutable();
    }

    public final ObservableBooleanValue hover() {
        return hover.toImmutable();
    }

    public final ObservableBooleanValue pressed() {
        return pressed.toImmutable();
    }

    public final void requestParentLayout() {
        Container container = parent().getValue();
        if (container != null && !container.isNeedsLayout()) {
            container.needsLayout();
        }
    }

    public float minWidth() {
        return prefWidth();
    }

    public float minHeight() {
        return prefHeight();
    }

    abstract public float prefWidth();

    abstract public float prefHeight();

    public float maxWidth() {
        return prefWidth();
    }

    public float maxHeight() {
        return prefHeight();
    }

    public boolean contains(int x, int y) {
        return x >= x().get() && x <= width().get() && y >= y().get() && y <= height().get();
    }

    public ComponentRenderer getRenderer() {
        if (renderer == null)
            renderer = createDefaultRenderer();
        return renderer;
    }

    protected abstract ComponentRenderer createDefaultRenderer();

    private ObservableMap<Object, Object> properties;

    public ObservableMap<Object, Object> getProperties() {
        if (properties == null) {
            properties = ObservableCollections.observableMap(new HashMap<>());
        }
        return properties;
    }

    public boolean hasProperties() {
        return properties != null && !properties.isEmpty();
    }

}
