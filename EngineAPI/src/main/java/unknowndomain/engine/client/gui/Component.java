package unknowndomain.engine.client.gui;

import com.github.mouse0w0.lib4j.observable.collection.ObservableCollections;
import com.github.mouse0w0.lib4j.observable.collection.ObservableMap;
import com.github.mouse0w0.lib4j.observable.value.*;
import unknowndomain.engine.client.gui.event.KeyEvent;
import unknowndomain.engine.client.gui.event.MouseEvent;
import unknowndomain.engine.client.gui.rendering.ComponentRenderer;
import unknowndomain.engine.client.input.keybinding.Key;
import unknowndomain.engine.event.Event;

import java.util.HashMap;

public abstract class Component {

    final MutableValue<Scene> scene = new SimpleMutableObjectValue<>();
    final MutableValue<Container> parent = new SimpleMutableObjectValue<>();

    private final MutableFloatValue x = new SimpleMutableFloatValue();
    private final MutableFloatValue y = new SimpleMutableFloatValue();
    final MutableFloatValue width = new SimpleMutableFloatValue();
    final MutableFloatValue height = new SimpleMutableFloatValue();

    protected final MutableBooleanValue visible = new SimpleMutableBooleanValue(true);
    protected final MutableBooleanValue disabled = new SimpleMutableBooleanValue(false);

    protected final MutableBooleanValue focused = new SimpleMutableBooleanValue(false);
    protected final MutableBooleanValue hover = new SimpleMutableBooleanValue(false);
    protected final MutableBooleanValue pressed = new SimpleMutableBooleanValue(false);

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

    public boolean contains(float posX, float posY){
        return (x().get() <= posX) &&
                (posX <= x().get() + width().get()) &&
                (y().get() <= posY) &&
                (posY <= y().get() + height().get());
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

    public void resize(float width, float height){
        this.width.set(width);
        this.height.set(height);
    }

    public void relocate(float x, float y){
        this.x.set(x);
        this.y.set(y);
    }

    public boolean hasProperties() {
        return properties != null && !properties.isEmpty();
    }

    public void handleEvent(Event event){
        if(!disabled.get()) {
            if (event instanceof MouseEvent.MouseEnterEvent) {
                hover.set(true);
            }
            if (event instanceof MouseEvent.MouseLeaveEvent) {
                hover.set(false);
                pressed.set(false);
            }
            if (event instanceof MouseEvent.MouseClickEvent) {
                var click = (MouseEvent.MouseClickEvent)event;
                if(click.getKey() == Key.MOUSE_BUTTON_LEFT){
                    pressed.set(true);
                    onClick(click);
                }
            }
            if (event instanceof MouseEvent.MouseReleasedEvent) {
                var release = (MouseEvent.MouseReleasedEvent)event;
                if(release.getKey() == Key.MOUSE_BUTTON_LEFT){
                    pressed.set(false);
                    onRelease(release);
                }
            }
        }
    }

    public void onRelease(MouseEvent.MouseReleasedEvent event) {

    }

    public void onClick(MouseEvent.MouseClickEvent event) {
    }

}
