package engine.event.misc;

import engine.event.Event;

@FunctionalInterface
public interface EventExceptionHandler {

    void handle(ListenerList list, RegisteredListener listener, Event event, Exception e);
}
