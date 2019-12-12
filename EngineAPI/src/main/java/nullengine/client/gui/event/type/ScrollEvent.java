package nullengine.client.gui.event.type;

import nullengine.client.gui.event.Event;
import nullengine.client.gui.event.EventTarget;
import nullengine.client.gui.event.EventType;

public class ScrollEvent extends MouseEvent {
    public static final EventType<ScrollEvent> ANY = new EventType<>("SCROLL", MouseEvent.ANY);

    private final double xScroll;
    private final double yScroll;

    public ScrollEvent(EventType<? extends Event> eventType, Object source, EventTarget target, float screenX, float screenY, float x, float y, double xScroll, double yScroll) {
        super(eventType, source, target, screenX, screenY, x, y);
        this.xScroll = xScroll;
        this.yScroll = yScroll;
    }

    public double getXScroll() {
        return xScroll;
    }

    public double getYScroll() {
        return yScroll;
    }
}
