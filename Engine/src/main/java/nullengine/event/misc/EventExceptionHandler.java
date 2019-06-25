package nullengine.event.misc;

import nullengine.event.Event;

@FunctionalInterface
public interface EventExceptionHandler {

    void handle(ListenerList list, RegisteredListener listener, Event event, Exception e);
}
