package engine.gui.input;

import engine.gui.event.Event;
import engine.gui.event.EventTarget;
import engine.gui.event.EventType;

import java.nio.file.Path;
import java.util.List;

public class DropEvent extends Event {
    public static final EventType<DropEvent> ANY = new EventType<>("DROP", EventType.ROOT);

    public static final EventType<DropEvent> DROP = ANY;

    private final float screenX;
    private final float screenY;
    private final float x;
    private final float y;

    private final List<Path> paths;

    public DropEvent(EventType<? extends Event> eventType, EventTarget target, float x, float y, float screenX, float screenY, List<Path> paths) {
        super(eventType, target);
        this.screenX = screenX;
        this.screenY = screenY;
        this.x = x;
        this.y = y;
        this.paths = paths;
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

    public List<Path> getPaths() {
        return paths;
    }

    @Override
    public String toString() {
        return "DropEvent{" +
                "eventType=" + getEventType() +
                ", target=" + getTarget() +
                ", consumed=" + isConsumed() +
                ", screenX=" + getScreenX() +
                ", screenY=" + getScreenY() +
                ", x=" + getX() +
                ", y=" + getY() +
                ", paths=" + getPaths() +
                '}';
    }
}