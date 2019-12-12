package nullengine.client.gui.event;

public abstract class Event implements Cloneable {

    private final EventType<? extends Event> eventType;
    private final Object source;
    private final EventTarget target;

    private boolean consumed;

    public Event(EventType<? extends Event> eventType, EventTarget target) {
        this(eventType, null, target);
    }

    public Event(EventType<? extends Event> eventType, Object source, EventTarget target) {
        this.eventType = eventType;
        this.source = source;
        this.target = target;
    }

    public EventType<? extends Event> getEventType() {
        return eventType;
    }

    public Object getSource() {
        return source;
    }

    public EventTarget getTarget() {
        return target;
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

    public Event fireEvent(EventTarget eventTargets) {
        return EventUtils.fireEvent(this, eventTargets);
    }
}
