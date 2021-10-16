package engine.event;

import engine.event.misc.EventExceptionHandler;
import engine.event.misc.EventListenerFactory;
import engine.event.misc.ListenerList;
import engine.event.misc.RegisteredListener;
import net.jodah.typetools.TypeResolver;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class SimpleEventBus implements EventBus {

    private final Map<Class<?>, ListenerList> listenerLists = new ConcurrentHashMap<>();
    private final Map<Object, List<RegisteredListener>> registeredListeners = new HashMap<>();

    private final EventExceptionHandler eventExceptionHandler;
    private final EventListenerFactory eventListenerFactory;

    public SimpleEventBus(EventExceptionHandler eventExceptionHandler, EventListenerFactory eventListenerFactory) {
        this.eventExceptionHandler = eventExceptionHandler;
        this.eventListenerFactory = eventListenerFactory;
    }

    @Override
    public boolean post(Event event) {
        ListenerList listenerList = getListenerList(event.getClass());
        for (RegisteredListener listener : listenerList.getListeners()) {
            try {
                listener.post(event);
            } catch (Exception e) {
                eventExceptionHandler.handle(listenerList, listener, event, e);
            }
        }
        return event.isCancellable() && ((Cancellable) event).isCancelled();
    }

    private ListenerList getListenerList(Class<?> eventType) {
        return listenerLists.computeIfAbsent(eventType, this::createListenerList);
    }

    private ListenerList createListenerList(Class<?> eventType) {
        ListenerList listenerList = new ListenerList(eventType);
        for (Map.Entry<Class<?>, ListenerList> entry : listenerLists.entrySet()) {
            if (entry.getKey().isAssignableFrom(eventType)) {
                listenerList.addParent(entry.getValue());
            } else if (eventType.isAssignableFrom(entry.getKey())) {
                listenerList.addChild(entry.getValue());
            }
        }
        return listenerList;
    }

    @Override
    public void register(Object target) {
        if (registeredListeners.containsKey(target)) {
            throw new IllegalStateException("Listener has been registered");
        }

        if (target instanceof Class) {
            registerClass((Class<?>) target);
        } else {
            registerObject(target);
        }
    }

    private void registerObject(Object obj) {
        List<RegisteredListener> listeners = new ArrayList<>();
        Arrays.stream(obj.getClass().getMethods())
                .filter(method -> !Modifier.isStatic(method.getModifiers()) && method.isAnnotationPresent(Listener.class))
                .map(method -> registerListener(obj, method, false))
                .forEach(listeners::add);
        registeredListeners.put(obj, listeners);
    }

    private void registerClass(Class<?> clazz) {
        List<RegisteredListener> listeners = new ArrayList<>();
        Arrays.stream(clazz.getMethods())
                .filter(method -> Modifier.isStatic(method.getModifiers()) && method.isAnnotationPresent(Listener.class))
                .map(method -> registerListener(clazz, method, true))
                .forEach(listeners::add);
        registeredListeners.put(clazz, listeners);
    }

    private RegisteredListener registerListener(Object owner, Method method, boolean isStatic) {
        if (method.getParameterCount() != 1) {
            throw new EventException(String.format("The count of listener method parameter must be 1. Listener: %s.%s(?)", method.getDeclaringClass().getName(), method.getName()));
        }

        Class<?> eventType = method.getParameterTypes()[0];
        if (!Event.class.isAssignableFrom(eventType)) {
            throw new EventException(String.format("The type of parameter of listener method must be Event or its sub class. Listener: %s.%s(%s)", method.getDeclaringClass().getName(), method.getName(), eventType.getName()));
        }

        if (method.getReturnType() != void.class) {
            throw new EventException(String.format("The return type of listener method must be void. Listener: %s.%s(%s)", method.getDeclaringClass().getName(), method.getName(), eventType.getName()));
        }

        if (!Modifier.isPublic(method.getModifiers())) {
            throw new EventException(String.format("Listener method must be public. Listener: %s.%s(%s)", method.getDeclaringClass().getName(), method.getName(), eventType.getName()));
        }

        Listener anno = method.getAnnotation(Listener.class);

        Type genericType = null;

        if (GenericEvent.class.isAssignableFrom(eventType)) {
            Type type = method.getGenericParameterTypes()[0];
            genericType = type instanceof ParameterizedType ? ((ParameterizedType) type).getActualTypeArguments()[0] : null;
            if (genericType instanceof ParameterizedType) {
                genericType = ((ParameterizedType) genericType).getRawType();
            }
        }

        try {
            RegisteredListener listener = new RegisteredListener(eventType, owner, anno.order(), anno.receiveCancelled(), genericType, eventListenerFactory.create(eventType, owner, method, isStatic));
            getListenerList(eventType).register(listener);
            return listener;
        } catch (Exception e) {
            throw new EventException(String.format("Cannot register listener. Listener: %s.%s(%s)", method.getDeclaringClass().getName(), method.getName(), method.getParameterTypes()[0].getName()), e);
        }
    }

    @Override
    public void unregister(Object target) {
        if (!registeredListeners.containsKey(target)) {
            return;
        }

        registeredListeners.remove(target).forEach(listener -> getListenerList(listener.getEventType()).unregister(listener));
    }

    @Override
    public <T extends Event> void addListener(Consumer<T> consumer) {
        addListener(Order.DEFAULT, consumer);
    }

    @Override
    public <T extends Event> void addListener(Order order, Consumer<T> consumer) {
        addListener(order, false, consumer);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Event> void addListener(Order order, boolean receiveCancelled, Consumer<T> consumer) {
        addListener(order, receiveCancelled, (Class<T>) TypeResolver.resolveRawArgument(Consumer.class, consumer.getClass()), consumer);
    }

    @Override
    public <T extends Event> void addListener(Order order, boolean receiveCancelled, Class<T> eventType, Consumer<T> consumer) {
        if (registeredListeners.containsKey(consumer)) {
            throw new IllegalStateException("Listener has been registered");
        }
        RegisteredListener listener = new RegisteredListener(eventType, consumer, order, receiveCancelled, null, event -> consumer.accept(eventType.cast(event)));
        registeredListeners.put(listener, List.of(listener));
        getListenerList(eventType).register(listener);
    }

    @Override
    public <T extends GenericEvent<? extends G>, G> void addGenericListener(Class<G> genericType, Consumer<T> consumer) {
        addGenericListener(genericType, Order.DEFAULT, consumer);
    }

    @Override
    public <T extends GenericEvent<? extends G>, G> void addGenericListener(Class<G> genericType, Order order, Consumer<T> consumer) {
        addGenericListener(genericType, order, false, consumer);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends GenericEvent<? extends G>, G> void addGenericListener(Class<G> genericType, Order order, boolean receiveCancelled, Consumer<T> consumer) {
        addGenericListener(genericType, order, receiveCancelled, (Class<T>) TypeResolver.resolveRawArgument(Consumer.class, consumer.getClass()), consumer);
    }

    @Override
    public <T extends GenericEvent<? extends G>, G> void addGenericListener(Class<G> genericType, Order order, boolean receiveCancelled, Class<T> eventType, Consumer<T> consumer) {
        if (registeredListeners.containsKey(consumer)) {
            throw new IllegalStateException("Listener has been registered");
        }
        RegisteredListener listener = new RegisteredListener(eventType, consumer, order, receiveCancelled, genericType, event -> consumer.accept(eventType.cast(event)));
        registeredListeners.put(listener, List.of(listener));
        getListenerList(eventType).register(listener);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private EventExceptionHandler eventExceptionHandler = (list, listener, event, e) -> {
            throw new EventException(String.format("Cannot handle event. EventType: %s", event.getClass().getName()), e);
        };
        private EventListenerFactory eventListenerFactory;

        private Builder() {
        }

        public Builder eventExceptionHandler(EventExceptionHandler eventExceptionHandler) {
            this.eventExceptionHandler = eventExceptionHandler;
            return this;
        }

        public Builder eventListenerFactory(EventListenerFactory eventListenerFactory) {
            this.eventListenerFactory = eventListenerFactory;
            return this;
        }

        public SimpleEventBus build() {
            return new SimpleEventBus(eventExceptionHandler, eventListenerFactory);
        }
    }
}
