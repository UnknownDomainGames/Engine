package engine.event;

import java.util.function.Consumer;

class ConsumeListenerInvoker<T> implements ListenerInvoker {
    private final Class<T> eventType;
    private final Consumer<T> consumer;

    public ConsumeListenerInvoker(Class<T> eventType, Consumer<T> consumer) {
        this.eventType = eventType;
        this.consumer = consumer;
    }

    @Override
    public void invoke(Event event) throws Throwable {
        consumer.accept(eventType.cast(event));
    }
}
