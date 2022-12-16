package engine.gui.input;

import engine.gui.event.Event;
import engine.gui.event.EventTarget;
import engine.gui.event.EventType;

import java.nio.file.Path;
import java.util.List;

public class DropEvent extends Event {
    public static final EventType<DropEvent> ANY = new EventType<>("DROP", EventType.ROOT);

    public static final EventType<DropEvent> DROP = ANY;

    private final double screenX;
    private final double screenY;
    private final double x;
    private final double y;

    private final List<Path> paths;

    public DropEvent(EventType<? extends Event> eventType, EventTarget target, double x, double y, double screenX, double screenY, List<Path> paths) {
        super(eventType, target);
        this.screenX = screenX;
        this.screenY = screenY;
        this.x = x;
        this.y = y;
        this.paths = paths;
    }

    public double getScreenX() {
        return screenX;
    }

    public double getScreenY() {
        return screenY;
    }

    public double getX() {
        return x;
    }

    public double getY() {
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