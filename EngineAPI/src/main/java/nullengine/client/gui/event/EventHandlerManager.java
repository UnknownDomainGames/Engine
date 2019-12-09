package nullengine.client.gui.event;

import java.util.HashMap;
import java.util.Map;

public class EventHandlerManager implements EventDispatcher {

    private final Map<EventType<?>, CompositeEventHandler<?>> eventHandlers = new HashMap<>();

    public <T extends Event> void addEventHandler(EventType<T> eventType, EventHandler<T> eventHandler) {
        getOrCreateEventHandler(eventType).getEventHandlers().add(eventHandler);
    }

    public <T extends Event> void removeEventHandler(EventType<T> eventType, EventHandler<T> eventHandler) {
        getOrCreateEventHandler(eventType).getEventHandlers().remove(eventHandler);
    }

    public <T extends Event> EventHandler<T> getEventHandler(EventType<T> eventType) {
        return (EventHandler<T>) eventHandlers.get(eventType);
    }

    private <T extends Event> CompositeEventHandler getOrCreateEventHandler(EventType<T> eventType) {
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
