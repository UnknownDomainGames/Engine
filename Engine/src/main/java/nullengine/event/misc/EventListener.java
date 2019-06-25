package nullengine.event.misc;

import nullengine.event.Event;

public interface EventListener {

    void post(Event event) throws Exception;
}
