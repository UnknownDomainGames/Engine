package nullengine.client.gui;

import com.github.mouse0w0.observable.collection.ObservableCollections;
import com.github.mouse0w0.observable.collection.ObservableMap;
import com.github.mouse0w0.observable.value.*;
import nullengine.client.gui.event.FocusEvent;
import nullengine.client.gui.event.MouseEvent;
import nullengine.client.gui.rendering.ComponentRenderer;
import nullengine.client.input.keybinding.Key;
import nullengine.event.Event;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class Component {

    final MutableObjectValue<Scene> scene = new SimpleMutableObjectValue<>();
    final MutableObjectValue<Container> parent = new SimpleMutableObjectValue<>();

    private final MutableFloatValue x = new SimpleMutableFloatValue();
    private final MutableFloatValue y = new SimpleMutableFloatValue();

    public static final float FOLLOW_PARENT = -1;
    final MutableFloatValue width = new SimpleMutableFloatValue();
    final MutableFloatValue height = new SimpleMutableFloatValue();

    protected final MutableBooleanValue visible = new SimpleMutableBooleanValue(true);
    protected final MutableBooleanValue disabled = new SimpleMutableBooleanValue(false);

    protected final MutableBooleanValue focused = new SimpleMutableBooleanValue(false);
    protected final MutableBooleanValue hover = new SimpleMutableBooleanValue(false);
    protected final MutableBooleanValue pressed = new SimpleMutableBooleanValue(false);

    private ComponentRenderer renderer;

    public Component() {
        visible.addChangeListener((observable, oldValue, newValue) -> requestParentLayout());
    }

    public ObservableObjectValue<Scene> scene() {
        return scene;
    }

    public final ObservableObjectValue<Container> parent() {
        return parent.toUnmodifiable();
    }

    public final MutableFloatValue x() {
        return x;
    }

    public final MutableFloatValue y() {
        return y;
    }

    public final ObservableFloatValue width() {
        return width.toUnmodifiable();
    }

    public final ObservableFloatValue height() {
        return height.toUnmodifiable();
    }

    public final MutableBooleanValue visible() {
        return visible;
    }

    public final MutableBooleanValue disabled() {
        return disabled;
    }

    public final ObservableBooleanValue focused() {
        return focused.toUnmodifiable();
    }

    public final ObservableBooleanValue hover() {
        return hover.toUnmodifiable();
    }

    public final ObservableBooleanValue pressed() {
        return pressed.toUnmodifiable();
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

    public boolean contains(float posX, float posY) {
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

    public void overrideRenderer(ComponentRenderer r) {
        renderer = r;
    }

    protected abstract ComponentRenderer createDefaultRenderer();

    private ObservableMap<Object, Object> properties;

    public ObservableMap<Object, Object> getProperties() {
        if (properties == null) {
            properties = ObservableCollections.observableMap(new HashMap<>());
        }
        return properties;
    }

    public boolean isResizable() {
        return false;
    }

    public void resize(float width, float height) {
        this.width.set(width);
        this.height.set(height);
    }

    public void relocate(float x, float y) {
        this.x.set(x);
        this.y.set(y);
    }

    public boolean hasProperties() {
        return properties != null && !properties.isEmpty();
    }

    public void handleEvent(Event event) {
        if (!disabled.get()) {
            if (event instanceof MouseEvent.MouseEnterEvent) {
                hover.set(true);
            } else if (event instanceof MouseEvent.MouseLeaveEvent) {
                hover.set(false);
                pressed.set(false);
            } else if (event instanceof MouseEvent.MouseClickEvent) {
                var click = (MouseEvent.MouseClickEvent) event;
                if (click.getKey() == Key.MOUSE_BUTTON_LEFT) {
                    pressed.set(true);
                    onClick(click);
                }
            } else if (event instanceof MouseEvent.MouseReleasedEvent) {
                var release = (MouseEvent.MouseReleasedEvent) event;
                if (release.getKey() == Key.MOUSE_BUTTON_LEFT) {
                    pressed.set(false);
                    onRelease(release);
                }
            } else if (event instanceof FocusEvent) {
                if (event instanceof FocusEvent.FocusGainEvent) {
                    focused.set(true);
                } else if (event instanceof FocusEvent.FocusLostEvent) {
                    focused.set(false);
                }
            }
            if (handlers.containsKey(event.getClass())) {
                for (Consumer consumer : handlers.get(event.getClass())) {
                    consumer.accept(event);
                }
            }
        }
//
//        if(parent().getValue() instanceof Container)
//            this.parent().getValue().handleEvent(event);
    }

    public void onRelease(MouseEvent.MouseReleasedEvent event) {

    }

    public void onClick(MouseEvent.MouseClickEvent event) {
    }

    public void forceFocus() {
        focused.set(true);
    }

    public Pair<Float, Float> relativePos(float x, float y) {
        if (parent().isEmpty()) {
            return Pair.of(x, y);
        } else {
            return parent().getValue().relativePos(x - x().get(), y - y().get());
        }
    }

    private Map<Class<? extends Event>, List<Consumer>> handlers = new HashMap<>();

    public <T extends Event> void addEventHandler(Class<T> clazz, Consumer<T> handler) {
        if (!handlers.containsKey(clazz)) {
            handlers.put(clazz, new ArrayList<>());
        }
        handlers.get(clazz).add(handler);
    }

    public <T extends Event> void removeEventHandler(Class<T> clazz, Consumer<T> handler) {
        if (handlers.containsKey(clazz)) {
            handlers.get(clazz).remove(handler);
        }
    }

}
