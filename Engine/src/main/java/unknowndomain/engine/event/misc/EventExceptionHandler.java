package unknowndomain.engine.event.misc;

import unknowndomain.engine.event.Event;

@FunctionalInterface
public interface EventExceptionHandler {

    void handle(ListenerList list, RegisteredListener listener, Event event, Exception e);
}
