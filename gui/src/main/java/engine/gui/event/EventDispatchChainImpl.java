package engine.gui.event;

import java.util.LinkedList;

public class EventDispatchChainImpl implements EventDispatchChain {

    private final LinkedList<EventDispatcher> eventDispatchers = new LinkedList<>();

    @Override
    public EventDispatchChain append(EventDispatcher eventDispatcher) {
        eventDispatchers.addLast(eventDispatcher);
        return this;
    }

    @Override
    public EventDispatchChain prepend(EventDispatcher eventDispatcher) {
        eventDispatchers.addFirst(eventDispatcher);
        return this;
    }

    @Override
    public Event dispatchEvent(Event event) {
        for (EventDispatcher eventDispatcher : eventDispatchers) {
            eventDispatcher.dispatchEvent(event, this);
        }
        return event;
    }

    public void reset() {
        eventDispatchers.clear();
    }
}
