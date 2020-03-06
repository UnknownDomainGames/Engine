package engine.gui.input;

import engine.gui.event.Event;
import engine.gui.event.EventTarget;
import engine.gui.event.EventType;
import engine.input.Modifiers;
import engine.input.MouseButton;

public class MouseActionEvent extends MouseEvent {
    public static final EventType<MouseActionEvent> ANY = new EventType<>("MOUSE_ACTION", MouseEvent.ANY);

    public static final EventType<MouseActionEvent> MOUSE_PRESSED = new EventType<>("MOUSE_PRESSED", ANY);

    public static final EventType<MouseActionEvent> MOUSE_RELEASED = new EventType<>("MOUSE_RELEASED", ANY);

    public static final EventType<MouseActionEvent> MOUSE_CLICKED = new EventType<>("MOUSE_CLICKED", ANY);

    private final MouseButton button;
    private final Modifiers modifier;

    public MouseActionEvent(EventType<? extends Event> eventType, EventTarget target, float screenX, float screenY, float x, float y, MouseButton button, Modifiers modifier) {
        super(eventType, target, screenX, screenY, x, y);
        this.button = button;
        this.modifier = modifier;
    }

    public MouseButton getButton() {
        return button;
    }

    public Modifiers getModifier() {
        return modifier;
    }

    @Override
    public String toString() {
        return "MouseActionEvent{" +
                "eventType=" + getEventType() +
                ", target=" + getTarget() +
                ", consumed=" + isConsumed() +
                ", screenX=" + getScreenX() +
                ", screenY=" + getScreenY() +
                ", x=" + getX() +
                ", y=" + getY() +
                ", button=" + getButton() +
                ", modifier=" + getModifier() +
                '}';
    }
}
