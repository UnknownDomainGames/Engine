package nullengine.client.gui.input;

import nullengine.client.gui.event.Event;
import nullengine.client.gui.event.EventTarget;
import nullengine.client.gui.event.EventType;

public class MouseActionEvent extends MouseEvent {
    public static final EventType<MouseActionEvent> ANY = new EventType<>("MOUSE_ACTION", MouseEvent.ANY);

    public static final EventType<MouseActionEvent> MOUSE_PRESSED = new EventType<>("MOUSE_PRESSED", ANY);

    public static final EventType<MouseActionEvent> MOUSE_RELEASED = new EventType<>("MOUSE_RELEASED", ANY);

    public static final EventType<MouseActionEvent> MOUSE_CLICKED = new EventType<>("MOUSE_CLICKED", ANY);

    private final MouseButton button;
    private final Modifiers modifier;

    public MouseActionEvent(EventType<? extends Event> eventType, Object source, EventTarget target, float screenX, float screenY, float x, float y, MouseButton button, Modifiers modifier) {
        super(eventType, source, target, screenX, screenY, x, y);
        this.button = button;
        this.modifier = modifier;
    }

    public MouseButton getButton() {
        return button;
    }

    public Modifiers getModifier() {
        return modifier;
    }
}
