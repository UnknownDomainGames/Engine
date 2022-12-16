package engine.gui.input;

import engine.gui.event.Event;
import engine.gui.event.EventTarget;
import engine.gui.event.EventType;

public class ScrollEvent extends MouseEvent {
    public static final EventType<ScrollEvent> ANY = new EventType<>("SCROLL", MouseEvent.ANY);
    public static final EventType<ScrollEvent> SCROLL = ANY;

    private final double xScroll;
    private final double yScroll;

    public ScrollEvent(EventType<? extends Event> eventType, EventTarget target, double screenX, double screenY, double x, double y, double xScroll, double yScroll) {
        super(eventType, target, screenX, screenY, x, y);
        this.xScroll = xScroll;
        this.yScroll = yScroll;
    }

    public double getXScroll() {
        return xScroll;
    }

    public double getYScroll() {
        return yScroll;
    }

    @Override
    public String toString() {
        return "ScrollEvent{" +
                "eventType=" + getEventType() +
                ", target=" + getTarget() +
                ", consumed=" + isConsumed() +
                ", screenX=" + getScreenX() +
                ", screenY=" + getScreenY() +
                ", x=" + getX() +
                ", y=" + getY() +
                ", xScroll=" + getXScroll() +
                ", yScroll=" + getYScroll() +
                '}';
    }
}
