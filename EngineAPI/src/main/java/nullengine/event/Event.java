package nullengine.event;

import java.util.Optional;

/**
 * Every event class should implement this interface.
 */
public interface Event {
    default boolean isCancellable() {
        return this instanceof Cancellable;
    }

    Optional<Event> getCausality();

    void setCausality(Event event);

    default boolean isCauseBy(Class<? extends Event> cause) {
        Event event = getCausality().orElse(null);
        if (event != null) {
            if (cause.isAssignableFrom(event.getClass())) {
                return true;
            }
            return event.isCauseBy(cause);
        }
        return false;
    }
}
