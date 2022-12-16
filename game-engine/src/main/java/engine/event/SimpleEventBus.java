package engine.event;

import net.jodah.typetools.TypeResolver;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SimpleEventBus implements EventBus {
    private final Map<Class<?>, ListenerList> listenerLists = new HashMap<>();
    private final Map<Object, ListenerWrapper[]> ownerToListeners = new HashMap<>();
    private final EventExceptionHandler exceptionHandler;

    public SimpleEventBus() {
        this.exceptionHandler = EventExceptionHandler.PRINT;
    }

    public SimpleEventBus(EventExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public boolean post(Event event) {
        ListenerList listenerList = getListenerList(event.getClass());
        for (ListenerWrapper listener : listenerList) {
            try {
                listener.post(event);
            } catch (Throwable t) {
                exceptionHandler.handle(event, t);
            }
        }
        return event.isCancellable() && ((Cancellable) event).isCancelled();
    }

    private ListenerList getListenerList(Class<?> eventType) {
        return listenerLists.computeIfAbsent(eventType, this::createListenerList);
    }

    private ListenerList createListenerList(Class<?> eventType) {
        ListenerList listenerList = new ListenerList();
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
        if (ownerToListeners.containsKey(target)) {
            throw new IllegalStateException("Listener has been registered");
        }

        if (target instanceof Class) {
            registerClass((Class<?>) target);
        } else {
            registerObject(target);
        }
    }

    private void registerObject(Object obj) {
        List<ListenerWrapper> listeners = new ArrayList<>();
        for (Method method : obj.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Listener.class) && !Modifier.isStatic(method.getModifiers())) {
                listeners.add(registerListener(obj, method, false));
            }
        }
        ownerToListeners.put(obj, listeners.toArray(new ListenerWrapper[listeners.size()]));
    }

    private void registerClass(Class<?> clazz) {
        List<ListenerWrapper> listeners = new ArrayList<>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Listener.class) && Modifier.isStatic(method.getModifiers())) {
                listeners.add(registerListener(clazz, method, true));
            }
        }
        ownerToListeners.put(clazz, listeners.toArray(new ListenerWrapper[listeners.size()]));
    }

    private ListenerWrapper registerListener(Object owner, Method method, boolean isStatic) {
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

        // Get generic type.
        Type genericType = null;
        if (GenericEvent.class.isAssignableFrom(eventType)) {
            Type type = method.getGenericParameterTypes()[0];
            genericType = type instanceof ParameterizedType ? ((ParameterizedType) type).getActualTypeArguments()[0] : null;
            if (genericType instanceof ParameterizedType) {
                genericType = ((ParameterizedType) genericType).getRawType();
            }
        }

        Listener listenerAnno = method.getAnnotation(Listener.class);
        try {
            ListenerWrapper listener = new ListenerWrapper(eventType, genericType, listenerAnno.order(), listenerAnno.receiveCancelled(), createInvoker(owner, method, isStatic));
            getListenerList(eventType).register(listener);
            return listener;
        } catch (Exception e) {
            throw new EventException(String.format("Cannot register listener. Listener: %s.%s(%s)", method.getDeclaringClass().getName(), method.getName(), method.getParameterTypes()[0].getName()), e);
        }
    }

    protected ListenerInvoker createInvoker(Object owner, Method method, boolean isStatic) throws Exception {
        MethodHandle handle = MethodHandles.publicLookup().unreflect(method);
        return new MethodHandleListenerInvoker(isStatic ? handle : handle.bindTo(owner));
    }

    @Override
    public void unregister(Object target) {
        ListenerWrapper[] listeners = ownerToListeners.remove(target);
        if (listeners != null) {
            for (ListenerWrapper listener : listeners) {
                getListenerList(listener.getEventType()).unregister(listener);
            }
        }
    }

    @Override
    public <T extends Event> void addListener(Consumer<T> consumer) {
        addListener(Order.DEFAULT, false, consumer);
    }

    @Override
    public <T extends Event> void addListener(Order order, Consumer<T> consumer) {
        addListener(order, false, consumer);
    }

    @Override
    public <T extends Event> void addListener(Order order, boolean receiveCancelled, Consumer<T> consumer) {
        addListener(order, receiveCancelled, (Class<T>) TypeResolver.resolveRawArgument(Consumer.class, consumer.getClass()), consumer);
    }

    @Override
    public <T extends Event> void addListener(Order order, boolean receiveCancelled, Class<T> eventType, Consumer<T> consumer) {
        if (ownerToListeners.containsKey(consumer)) {
            throw new IllegalStateException("Listener has been registered.");
        }
        ListenerWrapper listener = new ListenerWrapper(eventType, null, order, receiveCancelled, new ConsumeListenerInvoker<>(eventType, consumer));
        getListenerList(eventType).register(listener);
        ownerToListeners.put(consumer, new ListenerWrapper[]{listener});
    }

    @Override
    public <T extends GenericEvent<? extends G>, G> void addGenericListener(Class<G> genericType, Consumer<T> consumer) {
        addGenericListener(genericType, Order.DEFAULT, false, consumer);
    }

    @Override
    public <T extends GenericEvent<? extends G>, G> void addGenericListener(Class<G> genericType, Order order, Consumer<T> consumer) {
        addGenericListener(genericType, order, false, consumer);
    }

    @Override
    public <T extends GenericEvent<? extends G>, G> void addGenericListener(Class<G> genericType, Order order, boolean receiveCancelled, Consumer<T> consumer) {
        addGenericListener(genericType, order, receiveCancelled, (Class<T>) TypeResolver.resolveRawArgument(Consumer.class, consumer.getClass()), consumer);
    }

    @Override
    public <T extends GenericEvent<? extends G>, G> void addGenericListener(Class<G> genericType, Order order, boolean receiveCancelled, Class<T> eventType, Consumer<T> consumer) {
        if (ownerToListeners.containsKey(consumer)) {
            throw new IllegalStateException("Listener has been registered.");
        }
        ListenerWrapper listener = new ListenerWrapper(eventType, genericType, order, receiveCancelled, new ConsumeListenerInvoker<>(eventType, consumer));
        getListenerList(eventType).register(listener);
        ownerToListeners.put(consumer, new ListenerWrapper[]{listener});
    }
}
