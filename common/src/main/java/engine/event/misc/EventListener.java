package engine.event.misc;

import engine.event.Event;

public interface EventListener {

    void post(Event event) throws Exception;
}
