package nullengine.event.cause;

import nullengine.event.Event;

import java.util.Optional;

/**
 * Represent the causality of an event.
 */
public interface Cause {
    /**
     * The causality event (parent event)
     */
    Optional<Event> getCausality();
}
