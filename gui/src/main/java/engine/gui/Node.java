package engine.gui;

import com.github.mouse0w0.observable.collection.ObservableCollections;
import com.github.mouse0w0.observable.collection.ObservableMap;
import com.github.mouse0w0.observable.value.*;
import engine.gui.event.*;
import engine.gui.graphics.Graphics;
import engine.gui.graphics.NodeRenderer;
import engine.gui.input.*;
import engine.gui.misc.Bounds;
import engine.gui.misc.Point;
import engine.gui.stage.Stage;
import engine.input.MouseButton;

import java.util.HashMap;

public abstract class Node implements EventTarget {

    final MutableObjectValue<Scene> scene = new SimpleMutableObjectValue<>();
    final MutableObjectValue<Parent> parent = new SimpleMutableObjectValue<>();

    private MutableFloatValue layoutX;
    private MutableFloatValue layoutY;

    private final MutableFloatValue width = new SimpleMutableFloatValue();
    private final MutableFloatValue height = new SimpleMutableFloatValue();

    final MutableBooleanValue focused = new SimpleMutableBooleanValue(false);
    final MutableBooleanValue hover = new SimpleMutableBooleanValue(false);
    final MutableBooleanValue pressed = new SimpleMutableBooleanValue(false);

    private MutableBooleanValue visible;
    private MutableBooleanValue disabled;

    @SuppressWarnings("rawtypes")
    private NodeRenderer renderer;

    private EventHandlerManager eventHandlerManager = new EventHandlerManager();

    public Node() {
    }

    public final ObservableObjectValue<Scene> scene() {
        return scene;
    }

    public final Scene getScene() {
        return scene.get();
    }

    public final ObservableObjectValue<Parent> parent() {
        return parent.toUnmodifiable();
    }

    public final Parent getParent() {
        return parent.get();
    }

    public final void toFront() {
        parent().ifPresent(parent -> parent.toFront(this));
    }

    public final void toBack() {
        parent().ifPresent(parent -> parent.toBack(this));
    }

    public void resize(float width, float height) {
        this.width.set(width);
        this.height.set(height);
    }

    public final MutableFloatValue layoutX() {
        if (layoutX == null) {
            layoutX = new SimpleMutableFloatValue();
            layoutX.addChangeListener((observable, oldValue, newValue) -> requestParentLayout());
        }
        return layoutX;
    }

    public final float getLayoutX() {
        return layoutX == null ? 0 : layoutX.get();
    }

    public final void setLayoutX(float x) {
        layoutX().set(x);
    }

    public final MutableFloatValue layoutY() {
        if (layoutY == null) {
            layoutY = new SimpleMutableFloatValue();
            layoutY.addChangeListener((observable, oldValue, newValue) -> requestParentLayout());
        }
        return layoutY;
    }

    public final float getLayoutY() {
        return layoutX == null ? 0 : layoutY.get();
    }

    public final void setLayoutY(float y) {
        layoutY().set(y);
    }

    public final void relocate(float x, float y) {
        setLayoutX(x);
        setLayoutY(y);
    }

    public final ObservableFloatValue width() {
        return width;
    }

    public final float getWidth() {
        return width.get();
    }

    public final ObservableFloatValue height() {
        return height;
    }

    public final float getHeight() {
        return height.get();
    }

    public final ObservableBooleanValue focused() {
        return focused.toUnmodifiable();
    }

    public final boolean isFocused() {
        return focused.get();
    }

    public final ObservableBooleanValue hover() {
        return hover.toUnmodifiable();
    }

    public final boolean isHover() {
        return hover.get();
    }

    public final ObservableBooleanValue pressed() {
        return pressed.toUnmodifiable();
    }

    public final boolean isPressed() {
        return pressed.get();
    }

    public final MutableBooleanValue visible() {
        if (visible == null) {
            visible = new SimpleMutableBooleanValue(true);
        }
        return visible;
    }

    public final boolean isVisible() {
        return visible == null || visible.get();
    }

    public final void setVisible(boolean visible) {
        visible().set(visible);
    }

    public final MutableBooleanValue disabled() {
        if (disabled == null) {
            disabled = new SimpleMutableBooleanValue(false);
        }
        return disabled;
    }

    public final boolean isDisabled() {
        return disabled != null && disabled.get();
    }

    public final void setDisabled(boolean disabled) {
        disabled().set(disabled);
    }

    public final void requestParentLayout() {
        Parent parent = getParent();
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

    public boolean contains(Point point) {
        return contains(point.getX(), point.getY());
    }

    public boolean contains(float x, float y) {
        return x >= getLayoutX() && x <= getLayoutX() + getWidth() && y >= getLayoutY() && y <= getLayoutY() + getHeight();
    }

    @SuppressWarnings("unchecked")
    public void doRender(Graphics graphics) {
        getRenderer().doRender(this, graphics);
    }

    @SuppressWarnings("rawtypes")
    public NodeRenderer getRenderer() {
        if (renderer == null)
            renderer = createDefaultRenderer();
        return renderer;
    }

    @SuppressWarnings("rawtypes")
    protected abstract NodeRenderer createDefaultRenderer();

    private ObservableMap<Object, Object> properties;

    public final ObservableMap<Object, Object> getProperties() {
        if (properties == null) {
            properties = ObservableCollections.observableMap(new HashMap<>());
        }
        return properties;
    }

    public boolean hasProperties() {
        return properties != null && !properties.isEmpty();
    }

    public final Bounds getBoundsInLocal() {
        return new Bounds(0, 0, getWidth(), getHeight());
    }

    public final Bounds getBoundsInParent() {
        return new Bounds(getLayoutX(), getLayoutY(), getWidth(), getHeight());
    }

    public final Bounds getBoundsInScene() {
        float minX = 0, minY = 0;
        Node node = this;
        while (node != null) {
            minX += node.getLayoutX();
            minY += node.getLayoutY();
            node = node.getParent();
        }
        return new Bounds(minX, minY, getWidth(), getHeight());
    }

    public final Bounds getBoundsInScreen() {
        float minX = 0, minY = 0;
        Node node = this;
        while (node != null) {
            minX += node.getLayoutX();
            minY += node.getLayoutY();
            node = node.getParent();
        }
        Stage stage = getScene().getStage();
        float scaleX = stage.getScaleX();
        float scaleY = stage.getScaleY();
        minX = minX * scaleX + stage.getX();
        minY = minY * scaleY + stage.getY();
        return new Bounds(minX, minY, getWidth() * scaleX, getHeight() * scaleY);
    }

    public void forceFocus() {
        focused.set(true);
    }

    public Point relativePos(float x, float y) {
        if (parent().isEmpty()) {
            return new Point(x - getLayoutX(), y - getLayoutY());
        } else {
            return getParent().relativePos(x - getLayoutX(), y - getLayoutY());
        }
    }

    public final EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
        if (preprocessEventHandler == null) {
            preprocessEventHandler = (event, eventDispatchChain) -> {
                preprocessEvent(event);
                return event;
            };
        }
        tail.append(preprocessEventHandler);

        var node = this;
        while (node != null) {
            tail = tail.append(node.eventHandlerManager);
            node = node.parent.get();
        }

        Scene scene = getScene();
        if (scene != null) {
            tail = scene.buildEventDispatchChain(tail);
        }
        return tail;
    }

    private EventDispatcher preprocessEventHandler;

    private void preprocessEvent(Event event) {
        if (!(event instanceof MouseEvent)) return;

        EventType<? extends Event> eventType = event.getEventType();
        if (eventType == MouseActionEvent.MOUSE_PRESSED) {
            if (((MouseActionEvent) event).getButton() != MouseButton.MOUSE_BUTTON_PRIMARY) return;
            for (var node = this; node != null; node = node.getParent()) {
                node.pressed.set(true);
            }
            return;
        }

        if (eventType == MouseActionEvent.MOUSE_RELEASED) {
            if (((MouseActionEvent) event).getButton() != MouseButton.MOUSE_BUTTON_PRIMARY) return;
            for (var node = this; node != null; node = node.getParent()) {
                node.pressed.set(false);
            }
            return;
        }

        if (event.getTarget() == this) {
            if (eventType == MouseEvent.MOUSE_ENTERED) {
                hover.set(true);
            } else if (eventType == MouseEvent.MOUSE_EXITED) {
                hover.set(false);
            }
        }
    }

    public final <T extends Event> void addEventHandler(EventType<T> eventType, EventHandler<T> eventHandler) {
        eventHandlerManager.addEventHandler(eventType, eventHandler);
    }

    public final <T extends Event> void removeEventHandler(EventType<T> eventType, EventHandler<T> eventHandler) {
        eventHandlerManager.removeEventHandler(eventType, eventHandler);
    }

    // ===== Event handlers =====
    public final MutableObjectValue<EventHandler<MouseEvent>> onMouseEntered() {
        return eventHandlerManager.onMouseEntered();
    }

    public final EventHandler<MouseEvent> getOnMouseEntered() {
        return eventHandlerManager.getOnMouseEntered();
    }

    public final void setOnMouseEntered(EventHandler<MouseEvent> onMouseEntered) {
        eventHandlerManager.setOnMouseEntered(onMouseEntered);
    }

    public final MutableObjectValue<EventHandler<MouseEvent>> onMouseExited() {
        return eventHandlerManager.onMouseExited();
    }

    public final EventHandler<MouseEvent> getOnMouseExited() {
        return eventHandlerManager.getOnMouseExited();
    }

    public final void setOnMouseExited(EventHandler<MouseEvent> onMouseExited) {
        eventHandlerManager.setOnMouseExited(onMouseExited);
    }

    public final MutableObjectValue<EventHandler<MouseEvent>> onMouseMoved() {
        return eventHandlerManager.onMouseMoved();
    }

    public final EventHandler<MouseEvent> getOnMouseMoved() {
        return eventHandlerManager.getOnMouseMoved();
    }

    public final void setOnMouseMoved(EventHandler<MouseEvent> onMouseMoved) {
        eventHandlerManager.setOnMouseMoved(onMouseMoved);
    }

    public final MutableObjectValue<EventHandler<MouseActionEvent>> onMousePressed() {
        return eventHandlerManager.onMousePressed();
    }

    public final EventHandler<MouseActionEvent> getOnMousePressed() {
        return eventHandlerManager.getOnMousePressed();
    }

    public final void setOnMousePressed(EventHandler<MouseActionEvent> onMousePressed) {
        eventHandlerManager.setOnMousePressed(onMousePressed);
    }

    public final MutableObjectValue<EventHandler<MouseActionEvent>> onMouseReleased() {
        return eventHandlerManager.onMouseReleased();
    }

    public final EventHandler<MouseActionEvent> getOnMouseReleased() {
        return eventHandlerManager.getOnMouseReleased();
    }

    public final void setOnMouseReleased(EventHandler<MouseActionEvent> onMouseReleased) {
        eventHandlerManager.setOnMouseReleased(onMouseReleased);
    }

    public final MutableObjectValue<EventHandler<MouseActionEvent>> onMouseClicked() {
        return eventHandlerManager.onMouseClicked();
    }

    public final EventHandler<MouseActionEvent> getOnMouseClicked() {
        return eventHandlerManager.getOnMouseClicked();
    }

    public final void setOnMouseClicked(EventHandler<MouseActionEvent> onMouseClicked) {
        eventHandlerManager.setOnMouseClicked(onMouseClicked);
    }

    public final MutableObjectValue<EventHandler<KeyEvent>> onKeyPressed() {
        return eventHandlerManager.onKeyPressed();
    }

    public final EventHandler<KeyEvent> getOnKeyPressed() {
        return eventHandlerManager.getOnKeyPressed();
    }

    public final void setOnKeyPressed(EventHandler<KeyEvent> onKeyPressed) {
        eventHandlerManager.setOnKeyPressed(onKeyPressed);
    }

    public final MutableObjectValue<EventHandler<KeyEvent>> onKeyReleased() {
        return eventHandlerManager.onKeyReleased();
    }

    public final EventHandler<KeyEvent> getOnKeyReleased() {
        return eventHandlerManager.getOnKeyReleased();
    }

    public final void setOnKeyReleased(EventHandler<KeyEvent> onKeyReleased) {
        eventHandlerManager.setOnKeyReleased(onKeyReleased);
    }

    public final MutableObjectValue<EventHandler<KeyEvent>> onKeyTyped() {
        return eventHandlerManager.onKeyTyped();
    }

    public final EventHandler<KeyEvent> getOnKeyTyped() {
        return eventHandlerManager.getOnKeyTyped();
    }

    public final void setOnKeyTyped(EventHandler<KeyEvent> onKeyTyped) {
        eventHandlerManager.setOnKeyTyped(onKeyTyped);
    }

    public final MutableObjectValue<EventHandler<ScrollEvent>> onScroll() {
        return eventHandlerManager.onScroll();
    }

    public final EventHandler<ScrollEvent> getOnScroll() {
        return eventHandlerManager.getOnScroll();
    }

    public final void setOnScroll(EventHandler<ScrollEvent> onScroll) {
        eventHandlerManager.setOnScroll(onScroll);
    }

    public MutableObjectValue<EventHandler<DropEvent>> onDrop() {
        return eventHandlerManager.onDrop();
    }

    public EventHandler<DropEvent> getOnDrop() {
        return eventHandlerManager.getOnDrop();
    }

    public void setOnDrop(EventHandler<DropEvent> onDrop) {
        eventHandlerManager.setOnDrop(onDrop);
    }
}
