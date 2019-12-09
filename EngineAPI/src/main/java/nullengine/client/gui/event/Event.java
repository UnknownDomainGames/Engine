package nullengine.client.gui.event;

public abstract class Event implements Cloneable {

    private final EventType<? extends Event> eventType;
    private final Object source;

    private boolean consumed;

    public Event(EventType<? extends Event> eventType) {
        this(eventType, null);
    }

    public Event(EventType<? extends Event> eventType, Object source) {
        this.eventType = eventType;
        this.source = source;
    }

    public EventType<? extends Event> getEventType() {
        return eventType;
    }

    public Object getSource() {
        return source;
    }

    public boolean isConsumed() {
        return consumed;
    }

    public void consume() {
        consumed = true;
    }

    public static Event fireEvent(Event event, EventTarget eventTarget) {
        return EventUtils.fireEvent(event, eventTarget);
    }

    public static Event fireEvent(Event event, EventTarget... eventTargets) {
        return EventUtils.fireEvent(event, eventTargets);
    }

    public Event fireEvent(EventTarget... eventTargets) {
        return EventUtils.fireEvent(this, eventTargets);
    }

    public Event fireEvent(EventTarget eventTargets) {
        return EventUtils.fireEvent(this, eventTargets);
    }
}
