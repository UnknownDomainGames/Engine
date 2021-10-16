package engine.gui.event;

import java.util.HashMap;
import java.util.Map;

public class BasicEventHandlerManager implements EventDispatcher {

    private final Map<EventType<? extends Event>, CompositeEventHandler<? extends Event>> eventHandlers = new HashMap<>();

    public <T extends Event> void addEventHandler(EventType<T> eventType, EventHandler<T> eventHandler) {
        getOrCreateEventHandler(eventType).getEventHandlers().add(eventHandler);
    }

    public <T extends Event> void removeEventHandler(EventType<T> eventType, EventHandler<T> eventHandler) {
        getOrCreateEventHandler(eventType).getEventHandlers().remove(eventHandler);
    }

    @SuppressWarnings("unchecked")
    public <T extends Event> EventHandler<T> getEventHandler(EventType<T> eventType) {
        return (EventHandler<T>) eventHandlers.get(eventType);
    }

    @SuppressWarnings("unchecked")
    private <T extends Event> CompositeEventHandler<T> getOrCreateEventHandler(EventType<T> eventType) {
        CompositeEventHandler<T> eventHandler = (CompositeEventHandler<T>) eventHandlers.get(eventType);
        if (eventHandler == null) {
            eventHandler = new CompositeEventHandler<>();
            eventHandlers.put(eventType, eventHandler);
        }
        return eventHandler;
    }

    @Override
    public Event dispatchEvent(Event event, EventDispatchChain eventDispatchChain) {
        var eventType = eventHandlers.get(event.getEventType());
        if (eventType != null)
            eventType.onRawEvent(event);
        return event;
    }
}
