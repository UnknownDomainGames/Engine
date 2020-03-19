package engine.gui;

import com.github.mouse0w0.observable.value.MutableObjectValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import engine.gui.event.BasicEventHandlerManager;
import engine.gui.event.Event;
import engine.gui.event.EventHandler;
import engine.gui.event.EventType;
import engine.gui.input.*;

final class EventHandlerManager extends BasicEventHandlerManager {

    private MutableObjectValue<EventHandler<MouseEvent>> onMouseEntered;

    public MutableObjectValue<EventHandler<MouseEvent>> onMouseEntered() {
        if (onMouseEntered == null) {
            onMouseEntered = new EventHandlerValue<>(MouseEvent.MOUSE_ENTERED);
        }
        return onMouseEntered;
    }

    public EventHandler<MouseEvent> getOnMouseEntered() {
        return onMouseEntered == null ? null : onMouseEntered.get();
    }

    public void setOnMouseEntered(EventHandler<MouseEvent> onMouseEntered) {
        onMouseEntered().set(onMouseEntered);
    }

    private MutableObjectValue<EventHandler<MouseEvent>> onMouseExited;

    public MutableObjectValue<EventHandler<MouseEvent>> onMouseExited() {
        if (onMouseExited == null) {
            onMouseExited = new EventHandlerValue<>(MouseEvent.MOUSE_EXITED);
        }
        return onMouseExited;
    }

    public EventHandler<MouseEvent> getOnMouseExited() {
        return onMouseExited == null ? null : onMouseExited.get();
    }

    public void setOnMouseExited(EventHandler<MouseEvent> onMouseExited) {
        onMouseExited().set(onMouseExited);
    }

    private MutableObjectValue<EventHandler<MouseEvent>> onMouseMoved;

    public MutableObjectValue<EventHandler<MouseEvent>> onMouseMoved() {
        if (onMouseMoved == null) {
            onMouseMoved = new EventHandlerValue<>(MouseEvent.MOUSE_MOVED);
        }
        return onMouseMoved;
    }

    public EventHandler<MouseEvent> getOnMouseMoved() {
        return onMouseMoved == null ? null : onMouseMoved.get();
    }

    public void setOnMouseMoved(EventHandler<MouseEvent> onMouseMoved) {
        onMouseMoved().set(onMouseMoved);
    }

    private MutableObjectValue<EventHandler<MouseActionEvent>> onMousePressed;

    public MutableObjectValue<EventHandler<MouseActionEvent>> onMousePressed() {
        if (onMousePressed == null) {
            onMousePressed = new EventHandlerValue<>(MouseActionEvent.MOUSE_PRESSED);
        }
        return onMousePressed;
    }

    public EventHandler<MouseActionEvent> getOnMousePressed() {
        return onMousePressed == null ? null : onMousePressed.get();
    }

    public void setOnMousePressed(EventHandler<MouseActionEvent> onMousePressed) {
        onMousePressed().set(onMousePressed);
    }

    private MutableObjectValue<EventHandler<MouseActionEvent>> onMouseReleased;

    public MutableObjectValue<EventHandler<MouseActionEvent>> onMouseReleased() {
        if (onMouseReleased == null) {
            onMouseReleased = new EventHandlerValue<>(MouseActionEvent.MOUSE_RELEASED);
        }
        return onMouseReleased;
    }

    public EventHandler<MouseActionEvent> getOnMouseReleased() {
        return onMouseReleased == null ? null : onMouseReleased.get();
    }

    public void setOnMouseReleased(EventHandler<MouseActionEvent> onMouseReleased) {
        onMouseReleased().set(onMouseReleased);
    }

    private MutableObjectValue<EventHandler<MouseActionEvent>> onMouseClicked;

    public MutableObjectValue<EventHandler<MouseActionEvent>> onMouseClicked() {
        if (onMouseClicked == null) {
            onMouseClicked = new EventHandlerValue<>(MouseActionEvent.MOUSE_CLICKED);
        }
        return onMouseClicked;
    }

    public EventHandler<MouseActionEvent> getOnMouseClicked() {
        return onMouseClicked == null ? null : onMouseClicked.get();
    }

    public void setOnMouseClicked(EventHandler<MouseActionEvent> onMouseClicked) {
        onMouseClicked().set(onMouseClicked);
    }

    private MutableObjectValue<EventHandler<KeyEvent>> onKeyPressed;

    public MutableObjectValue<EventHandler<KeyEvent>> onKeyPressed() {
        if (onKeyPressed == null) {
            onKeyPressed = new EventHandlerValue<>(KeyEvent.KEY_PRESSED);
        }
        return onKeyPressed;
    }

    public EventHandler<KeyEvent> getOnKeyPressed() {
        return onKeyPressed == null ? null : onKeyPressed.get();
    }

    public void setOnKeyPressed(EventHandler<KeyEvent> onKeyPressed) {
        onKeyPressed().set(onKeyPressed);
    }

    private MutableObjectValue<EventHandler<KeyEvent>> onKeyReleased;

    public MutableObjectValue<EventHandler<KeyEvent>> onKeyReleased() {
        if (onKeyReleased == null) {
            onKeyReleased = new EventHandlerValue<>(KeyEvent.KEY_RELEASED);
        }
        return onKeyReleased;
    }

    public EventHandler<KeyEvent> getOnKeyReleased() {
        return onKeyReleased == null ? null : onKeyReleased.get();
    }

    public void setOnKeyReleased(EventHandler<KeyEvent> onKeyReleased) {
        onKeyReleased().set(onKeyReleased);
    }

    private MutableObjectValue<EventHandler<KeyEvent>> onKeyTyped;

    public MutableObjectValue<EventHandler<KeyEvent>> onKeyTyped() {
        if (onKeyTyped == null) {
            onKeyTyped = new EventHandlerValue<>(KeyEvent.KEY_TYPED);
        }
        return onKeyTyped;
    }

    public EventHandler<KeyEvent> getOnKeyTyped() {
        return onKeyTyped == null ? null : onKeyTyped.get();
    }

    public void setOnKeyTyped(EventHandler<KeyEvent> onKeyTyped) {
        onKeyTyped().set(onKeyTyped);
    }

    private MutableObjectValue<EventHandler<ScrollEvent>> onScroll;

    public MutableObjectValue<EventHandler<ScrollEvent>> onScroll() {
        if (onScroll == null) {
            onScroll = new EventHandlerValue<>(ScrollEvent.SCROLL);
        }
        return onScroll;
    }

    public EventHandler<ScrollEvent> getOnScroll() {
        return onScroll == null ? null : onScroll.get();
    }

    public void setOnScroll(EventHandler<ScrollEvent> onScroll) {
        onScroll().set(onScroll);
    }

    private MutableObjectValue<EventHandler<DropEvent>> onDrop;

    public MutableObjectValue<EventHandler<DropEvent>> onDrop() {
        if (onDrop == null) {
            onDrop = new EventHandlerValue<>(DropEvent.DROP);
        }
        return onDrop;
    }

    public EventHandler<DropEvent> getOnDrop() {
        return onDrop == null ? null : onDrop.get();
    }

    public void setOnDrop(EventHandler<DropEvent> onDrop) {
        onDrop().set(onDrop);
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
