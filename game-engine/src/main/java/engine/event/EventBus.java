package engine.event;

import java.util.function.Consumer;

public interface EventBus {

    /**
     * Handle a event.
     *
     * @param event the event.
     * @return true if cancelled, false if not.
     */
    boolean post(Event event);

    /**
     * Register listeners.
     *
     * @param target the owner of listeners, object or class.
     */
    void register(Object target);

    /**
     * Unregister listeners.
     * @param target the owner of listeners, object or class.
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
