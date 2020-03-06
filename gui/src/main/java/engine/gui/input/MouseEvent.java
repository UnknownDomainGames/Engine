package engine.gui.input;

import engine.gui.event.Event;
import engine.gui.event.EventTarget;
import engine.gui.event.EventType;

public class MouseEvent extends Event {
    public static final EventType<MouseEvent> ANY = new EventType<>("MOUSE", EventType.ROOT);

    public static final EventType<MouseEvent> MOUSE = ANY;

    public static final EventType<MouseEvent> MOUSE_ENTERED = new EventType<>("MOUSE_ENTERED", ANY);

    public static final EventType<MouseEvent> MOUSE_EXITED = new EventType<>("MOUSE_EXITED", ANY);

    public static final EventType<MouseEvent> MOUSE_MOVED = new EventType<>("MOUSE_MOVED", ANY);

    private final float screenX;
    private final float screenY;
    private final float x;
    private final float y;

    public MouseEvent(EventType<? extends Event> eventType, EventTarget target, float x, float y, float screenX, float screenY) {
        super(eventType, target);
        this.screenX = screenX;
        this.screenY = screenY;
        this.x = x;
        this.y = y;
    }

    public float getScreenX() {
        return screenX;
    }

    public float getScreenY() {
        return screenY;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    @Override
    public String toString() {
        return "MouseEvent{" +
                "eventType=" + getEventType() +
                ", target=" + getTarget() +
                ", consumed=" + isConsumed() +
                ", screenX=" + getScreenX() +
                ", screenY=" + getScreenY() +
                ", x=" + getX() +
                ", y=" + getY() +
                '}';
    }
}
