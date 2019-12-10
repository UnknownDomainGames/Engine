package nullengine.client.gui.event;

import java.util.LinkedList;
import java.util.List;

public class CompositeEventHandler<T extends Event> implements EventHandler<T> {

    private final List<EventHandler<T>> eventHandlers = new LinkedList<>();

    public List<EventHandler<T>> getEventHandlers() {
        return eventHandlers;
    }

    @Override
    public void onEvent(T event) {
        for (EventHandler<T> eventHandler : eventHandlers) {
            eventHandler.onEvent(event);
            if (event.isConsumed()) return;

        }
    }

    void onRawEvent(Event event) {
        onEvent((T) event);
    }
}
