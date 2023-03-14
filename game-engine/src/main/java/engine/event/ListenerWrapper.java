package engine.event;

import java.lang.reflect.Type;
import java.util.function.Predicate;

final class ListenerWrapper {
    private static final Predicate<Event> ALWAYS_TRUE = event -> true;
    private static final Predicate<Event> NOT_CANCELLED = ListenerWrapper::notCancelled;

    private final Class<?> eventType;
    private final Type genericType;
    private final Order order;
    private final ListenerInvoker invoker;
    private final Predicate<Event> filter;

    public ListenerWrapper(Class<?> eventType, Type genericType, Order order, boolean receiveCancelled, ListenerInvoker invoker) {
        this.eventType = eventType;
        this.genericType = genericType;
        this.order = order;
        this.invoker = invoker;
        this.filter = createFilter(receiveCancelled, genericType != null);
    }

    private Predicate<Event> createFilter(boolean receiveCancelled, boolean isGeneric) {
        if (receiveCancelled) {
            if (isGeneric) {
                return this::checkGeneric;
            } else {
                return ALWAYS_TRUE;
            }
        } else {
            if (isGeneric) {
                return event -> notCancelled(event) && checkGeneric(event);
            } else {
                return NOT_CANCELLED;
            }
        }
    }

    public Class<?> getEventType() {
        return eventType;
    }

    public Order getOrder() {
        return order;
    }

    public void post(Event event) throws Throwable {
        if (filter.test(event)) {
            invoker.invoke(event);
        }
    }

    private static boolean notCancelled(Event event) {
        return !event.isCancellable() || !((Cancellable) event).isCancelled();
    }

    private boolean checkGeneric(Event event) {
        return ((GenericEvent<?>) event).getGenericType() == genericType;
    }
}
