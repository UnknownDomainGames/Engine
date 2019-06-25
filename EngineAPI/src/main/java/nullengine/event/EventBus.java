package nullengine.event;

import java.util.function.Consumer;

public interface EventBus {

    /**
     * Handle a event.
     *
     * @return True if cancelled, false if not.
     */
    boolean post(Event event);

    /**
     * Register listeners.
     */
    void register(Object target);

    /**
     * Unregister listeners.
     */
    void unregister(Object target);

    <T extends Event> void addListener(Consumer<T> consumer);

    <T extends Event> void addListener(Order order, Consumer<T> consumer);

    <T extends Event> void addListener(Order order, boolean receiveCancelled, Consumer<T> consumer);

    <T extends Event> void addListener(Order order, boolean receiveCancelled, Class<T> eventType, Consumer<T> consumer);

    <T extends GenericEvent<? extends G>, G> void addGenericListener(Class<G> genericType, Consumer<T> consumer);

    <T extends GenericEvent<? extends G>, G> void addGenericListener(Class<G> genericType, Order order, Consumer<T> consumer);

    <T extends GenericEvent<? extends G>, G> void addGenericListener(Class<G> genericType, Order order, boolean receiveCancelled, Consumer<T> consumer);

    <T extends GenericEvent<? extends G>, G> void addGenericListener(Class<G> genericType, Order order, boolean receiveCancelled, Class<T> eventType, Consumer<T> consumer);
}
