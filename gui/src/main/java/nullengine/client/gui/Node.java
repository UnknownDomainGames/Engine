package nullengine.client.gui;

import com.github.mouse0w0.observable.collection.ObservableCollections;
import com.github.mouse0w0.observable.collection.ObservableMap;
import com.github.mouse0w0.observable.value.*;
import nullengine.client.gui.event.*;
import nullengine.client.gui.input.KeyEvent;
import nullengine.client.gui.input.MouseActionEvent;
import nullengine.client.gui.input.MouseEvent;
import nullengine.client.gui.input.ScrollEvent;
import nullengine.client.gui.rendering.ComponentRenderer;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;

public abstract class Node implements EventTarget {

    final MutableObjectValue<Scene> scene = new SimpleMutableObjectValue<>();
    final MutableObjectValue<Parent> parent = new SimpleMutableObjectValue<>();

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

    private EventHandlerManager eventHandlerManager = new EventHandlerManager();

    public EventHandlerManager getEventHandlerManager() {
        return eventHandlerManager;
    }

    public Node() {
        visible.addChangeListener((observable, oldValue, newValue) -> requestParentLayout());
        this.addEventHandler(MouseEvent.MOUSE_ENTERED, (e) -> {
            if (disabled.get()) return;
            hover.set(true);
        });
        this.addEventHandler(MouseEvent.MOUSE_EXITED, (e) -> {
            hover.set(false);
            pressed.set(false);
        });
        this.addEventHandler(MouseActionEvent.MOUSE_PRESSED, (e) -> {
            if (disabled.get()) return;
            pressed.set(true);
        });
        this.addEventHandler(MouseActionEvent.MOUSE_RELEASED, (e) -> {
            pressed.set(false);
        });
    }

    public ObservableObjectValue<Scene> scene() {
        return scene;
    }

    public final ObservableObjectValue<Parent> parent() {
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
        Parent parent = parent().getValue();
        if (parent != null && !parent.isNeedsLayout()) {
            parent.needsLayout();
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

    public final EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
        var node = this;
        while (node != null) {
            tail.append(node.eventHandlerManager);
            node = node.parent.get();
        }
        return tail;
    }

    public <T extends Event> void addEventHandler(EventType<T> eventType, EventHandler<T> eventHandler) {
        eventHandlerManager.addEventHandler(eventType, eventHandler);
    }

    public <T extends Event> void removeEventHandler(EventType<T> eventType, EventHandler<T> eventHandler) {
        eventHandlerManager.removeEventHandler(eventType, eventHandler);
    }

    // ===== Event handlers =====
    private final MutableObjectValue<EventHandler<MouseEvent>> onMouseEntered = new EventHandlerValue<>(MouseEvent.MOUSE_ENTERED);

    public EventHandler<MouseEvent> getOnMouseEntered() {
        return onMouseEntered.get();
    }

    public void setOnMouseEntered(EventHandler<MouseEvent> onMouseEntered) {
        this.onMouseEntered.set(onMouseEntered);
    }

    private final MutableObjectValue<EventHandler<MouseEvent>> onMouseExited = new EventHandlerValue<>(MouseEvent.MOUSE_EXITED);

    public EventHandler<MouseEvent> getOnMouseExited() {
        return onMouseExited.get();
    }

    public void setOnMouseExited(EventHandler<MouseEvent> onMouseExited) {
        this.onMouseExited.set(onMouseExited);
    }

    private final MutableObjectValue<EventHandler<MouseEvent>> onMouseMoved = new EventHandlerValue<>(MouseEvent.MOUSE_MOVED);

    public EventHandler<MouseEvent> getOnMouseMoved() {
        return onMouseMoved.get();
    }

    public void setOnMouseMoved(EventHandler<MouseEvent> onMouseMoved) {
        this.onMouseMoved.set(onMouseMoved);
    }

    private final MutableObjectValue<EventHandler<MouseActionEvent>> onMousePressed = new EventHandlerValue<>(MouseActionEvent.MOUSE_PRESSED);

    public EventHandler<MouseActionEvent> getOnMousePressed() {
        return onMousePressed.get();
    }

    public void setOnMousePressed(EventHandler<MouseActionEvent> onMousePressed) {
        this.onMousePressed.set(onMousePressed);
    }

    private final MutableObjectValue<EventHandler<MouseActionEvent>> onMouseReleased = new EventHandlerValue<>(MouseActionEvent.MOUSE_CLICKED);

    public EventHandler<MouseActionEvent> getOnMouseReleased() {
        return onMouseReleased.get();
    }

    public void setOnMouseReleased(EventHandler<MouseActionEvent> onMouseReleased) {
        this.onMouseReleased.set(onMouseReleased);
    }

    private final MutableObjectValue<EventHandler<MouseActionEvent>> onMouseClicked = new EventHandlerValue<>(MouseActionEvent.MOUSE_CLICKED);

    public EventHandler<MouseActionEvent> getOnMouseClicked() {
        return onMouseClicked.get();
    }

    public void setOnMouseClicked(EventHandler<MouseActionEvent> onMouseClicked) {
        this.onMouseClicked.set(onMouseClicked);
    }

    private final MutableObjectValue<EventHandler<KeyEvent>> onKeyPressed = new EventHandlerValue<>(KeyEvent.KEY_PRESSED);

    public EventHandler<KeyEvent> getOnKeyPressed() {
        return onKeyPressed.get();
    }

    public void setOnKeyPressed(EventHandler<KeyEvent> onKeyPressed) {
        this.onKeyPressed.set(onKeyPressed);
    }

    private final MutableObjectValue<EventHandler<KeyEvent>> onKeyReleased = new EventHandlerValue<>(KeyEvent.KEY_RELEASED);

    public EventHandler<KeyEvent> getOnKeyReleased() {
        return onKeyReleased.get();
    }

    public void setOnKeyReleased(EventHandler<KeyEvent> onKeyReleased) {
        this.onKeyReleased.set(onKeyReleased);
    }

    private final MutableObjectValue<EventHandler<KeyEvent>> onKeyTyped = new EventHandlerValue<>(KeyEvent.KEY_TYPED);

    public EventHandler<KeyEvent> getOnKeyTyped() {
        return onKeyTyped.get();
    }

    public void setOnKeyTyped(EventHandler<KeyEvent> onKeyTyped) {
        this.onKeyTyped.set(onKeyTyped);
    }

    private final MutableObjectValue<EventHandler<ScrollEvent>> onScroll = new EventHandlerValue<>(ScrollEvent.ANY);

    public EventHandler<ScrollEvent> getOnScroll() {
        return onScroll.get();
    }

    public void setOnScroll(EventHandler<ScrollEvent> onScroll) {
        this.onScroll.set(onScroll);
    }

    private class EventHandlerValue<ET extends Event, T extends EventHandler<ET>> extends SimpleMutableObjectValue<T> {

        public EventHandlerValue(EventType<ET> eventType) {
            addChangeListener((observable, oldValue, newValue) -> {
                if (oldValue != null) removeEventHandler(eventType, oldValue);
                if (newValue != null) addEventHandler(eventType, newValue);
            });
        }
    }
}
