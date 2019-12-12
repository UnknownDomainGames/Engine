package nullengine.client.gui.event.type;

import nullengine.client.gui.event.Event;
import nullengine.client.gui.event.EventTarget;
import nullengine.client.gui.event.EventType;

public class MouseEvent extends Event {
    public static final EventType<MouseEvent> ANY = new EventType<>("MOUSE", EventType.ROOT);

    public static final EventType<MouseEvent> MOUSE_ENTERED = new EventType<>("MOUSE_ENTERED", ANY);

    public static final EventType<MouseEvent> MOUSE_EXITED = new EventType<>("MOUSE_EXITED", ANY);

    public static final EventType<MouseEvent> MOUSE_MOVED = new EventType<>("MOUSE_MOVED", ANY);

    private final double x;
    private final double y;

    public MouseEvent(EventType<? extends Event> eventType, Object source, EventTarget target, double x, double y) {
        super(eventType, source, target);
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
