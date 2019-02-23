package unknowndomain.engine.event.misc;

import unknowndomain.engine.event.Event;

public interface EventListener {

    void post(Event event) throws Exception;
}
