package nullengine.client.gui;

import com.github.mouse0w0.observable.value.MutableObjectValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import nullengine.client.gui.event.BasicEventHandlerManager;
import nullengine.client.gui.event.Event;
import nullengine.client.gui.event.EventHandler;
import nullengine.client.gui.event.EventType;
import nullengine.client.gui.input.KeyEvent;
import nullengine.client.gui.input.MouseActionEvent;
import nullengine.client.gui.input.MouseEvent;
import nullengine.client.gui.input.ScrollEvent;

final class EventHandlerManager extends BasicEventHandlerManager {

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
