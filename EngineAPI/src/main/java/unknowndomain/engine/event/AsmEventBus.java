package unknowndomain.engine.event;

import com.google.common.collect.ImmutableList;
import org.apache.commons.lang3.Validate;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import unknowndomain.engine.Platform;
import unknowndomain.engine.util.SafeClassDefiner;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.Map.Entry;

import static org.objectweb.asm.Opcodes.*;

public class AsmEventBus implements EventBus {

    private final Map<Class<?>, Map<Order, Collection<RegisteredListener>>> eventListeners = new HashMap<>();
    private final Map<Object, Collection<RegisteredListener>> registeredListeners = new HashMap<>();

    private int uniqueId = 0;

    @Override
    public boolean post(Event event) {
        Validate.notNull(event);

        Map<Order, Collection<RegisteredListener>> orderedExecutors = getListeners(event.getClass());
        if (orderedExecutors == null)
            return false;

        if (event.isCancellable()) {
            Cancellable cancellable = (Cancellable) event;

            for (Order order : Order.values()) {
                Collection<RegisteredListener> executors = orderedExecutors.get(order);
                if (executors == null)
                    continue;

                for (RegisteredListener executor : executors) {
                    if (!cancellable.isCancelled() || executor.isReceiveCancelled())
                        post(executor, event);
                }
            }
            return cancellable.isCancelled();
        } else {
            for (Order order : Order.values()) {
                Collection<RegisteredListener> executors = orderedExecutors.get(order);
                if (executors == null)
                    continue;

                for (RegisteredListener executor : executors) {
                    post(executor, event);
                }
            }
            return false;
        }
    }

    private Map<Order, Collection<RegisteredListener>> getListeners(Class<?> eventType) {
        Map<Order, Collection<RegisteredListener>> orderedListeners = eventListeners.get(eventType);
        if (orderedListeners != null)
            return orderedListeners;

        orderedListeners = new EnumMap<>(Order.class);
        for (Entry<Class<?>, Map<Order, Collection<RegisteredListener>>> entry : eventListeners.entrySet()) {
            if (!entry.getKey().isAssignableFrom(eventType))
                continue;

            for (Entry<Order, Collection<RegisteredListener>> entry0 : entry.getValue().entrySet()) {
                Collection<RegisteredListener> listeners = orderedListeners.get(entry0.getKey());
                if (listeners == null) {
                    listeners = new LinkedHashSet<>();
                    orderedListeners.put(entry0.getKey(), listeners);
                }
                listeners.addAll(entry0.getValue());
            }
        }
        eventListeners.put(eventType, orderedListeners);

        return orderedListeners;
    }

    private void post(RegisteredListener executor, Event event) {
        try {
            executor.invoke(event);
        } catch (Exception e) {
            Platform.getLogger().warn("Failed to handle event.", new EventException(e));
        }
    }

    @Override
    public void register(Object listener) {
        Validate.notNull(listener);

        if (registeredListeners.containsKey(listener))
            throw new EventException("Listener has been registered.");

        Collection<RegisteredListener> listenerExecutors = new LinkedList<>();

        Class<?> clazz = listener.getClass();
        for (Method method : clazz.getDeclaredMethods()) {
            Listener anno = method.getAnnotation(Listener.class);
            if (anno == null)
                continue;

            int modifiers = method.getModifiers();

            if (!Modifier.isPublic(modifiers) || Modifier.isStatic(modifiers) || Modifier.isAbstract(modifiers)) {
                continue; // TODO: Warning.
            }

            if (method.getParameterCount() != 1) {
                continue; // TODO: Warning.
            }

            Class<?> eventType = method.getParameterTypes()[0];
            if (!Event.class.isAssignableFrom(eventType)) {
                continue; // TODO: Warning.
            }

            try {
                RegisteredListener executor = createEventExecutor(method, listener, anno.order(),
                        anno.receiveCancelled(), eventType);
                listenerExecutors.add(executor);
                addEventListener(eventType, executor);
            } catch (ReflectiveOperationException e) {
                Platform.getLogger().warn(String.format("Failed to register listener %s.%s .",
                        listener.getClass().getSimpleName(), method.getName()), new EventException(e));
            }

        }

        registeredListeners.put(listener, ImmutableList.copyOf(listenerExecutors));
    }

    private void addEventListener(Class<?> eventType, RegisteredListener listener) {
        for (Entry<Class<?>, Map<Order, Collection<RegisteredListener>>> entry : eventListeners.entrySet()) {
            Class<?> childEventType = entry.getKey();
            if (!eventType.isAssignableFrom(childEventType))
                continue;

            addEventListener(entry.getValue(), listener);
        }

        if (!eventListeners.containsKey(eventType)) {
            Map<Order, Collection<RegisteredListener>> orderedListeners = new EnumMap<>(Order.class);
            eventListeners.put(eventType, orderedListeners);
            addEventListener(orderedListeners, listener);
        }
    }

    private void addEventListener(Map<Order, Collection<RegisteredListener>> orderedListeners,
                                  RegisteredListener listener) {
        Collection<RegisteredListener> listeners = orderedListeners.get(listener.getOrder());
        if (listeners == null) {
            listeners = new LinkedHashSet<>();
            orderedListeners.put(listener.getOrder(), listeners);
        }
        listeners.add(listener);
    }

    @Override
    public void unregister(Object listener) {
        Validate.notNull(listener);

        Collection<RegisteredListener> executors = registeredListeners.get(listener);
        if (executors == null)
            return;

        for (RegisteredListener executor : executors) {
            Class<?> eventType = executor.getEventType();
            for (Entry<Class<?>, Map<Order, Collection<RegisteredListener>>> entry : eventListeners.entrySet()) {
                Class<?> childEventType = entry.getKey();
                if (!eventType.isAssignableFrom(childEventType))
                    continue;

                entry.getValue().get(executor.getOrder()).remove(executor);
            }
        }
    }

    private RegisteredListener createEventExecutor(Method method, Object owner, Order order, boolean receiveCancelled,
                                                   Class<?> eventType) throws ReflectiveOperationException {
        Class<?> ownerType = owner.getClass();
        String ownerName = ownerType.getTypeName().replace('.', '/');
        String ownerDesc = Type.getDescriptor(ownerType);
        String eventName = eventType.getTypeName().replace('.', '/');
        String handlerDesc = Type.getMethodDescriptor(method);
        String handlerName = method.getName();
        String className = getUniqueName(ownerType.getSimpleName(), handlerName, eventType.getSimpleName());

        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;

        cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, className, null,
                "unknowndomain/engine/event/AsmEventBus$RegisteredListener", null);

        cw.visitSource(".dynamic", null);

        cw.visitInnerClass("unknowndomain/engine/event/AsmEventBus$RegisteredListener",
                "unknowndomain/engine/event/AsmEventBus", "EventExecutor", ACC_PUBLIC + ACC_STATIC + ACC_ABSTRACT);

        {
            fv = cw.visitField(ACC_PRIVATE + ACC_FINAL, "owner", ownerDesc, null, null);
            fv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>",
                    "(" + ownerDesc + "Ljava/lang/Class;ZLunknowndomain/engine/event/Order;)V",
                    "(" + ownerDesc + "Ljava/lang/Class<*>;ZLunknowndomain/engine/event/Order;)V", null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitVarInsn(ILOAD, 3);
            mv.visitVarInsn(ALOAD, 4);
            mv.visitMethodInsn(INVOKESPECIAL, "unknowndomain/engine/event/AsmEventBus$RegisteredListener", "<init>",
                    "(Ljava/lang/Class;ZLunknowndomain/engine/event/Order;)V", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitFieldInsn(PUTFIELD, className, "owner", ownerDesc);
            mv.visitInsn(RETURN);
            mv.visitMaxs(4, 5);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "getOwner", "()Ljava/lang/Object;", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, className, "owner", ownerDesc);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "invoke", "(Lunknowndomain/engine/event/Event;)V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, className, "owner", ownerDesc);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, eventName);
            mv.visitMethodInsn(INVOKEVIRTUAL, ownerName, handlerName, handlerDesc, false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        cw.visitEnd();

        Class<?> executorType = SafeClassDefiner.INSTANCE.defineClass(ownerType.getClassLoader(), className,
                cw.toByteArray());
        Constructor<?> executorConstructor = executorType.getConstructors()[0];
        return (RegisteredListener) executorConstructor.newInstance(owner, eventType, receiveCancelled, order);
    }

    private String getUniqueName(String ownerName, String handlerName, String eventName) {
        return "AsmEventBus_" + ownerName + "_" + handlerName + "_" + eventName + "_" + (uniqueId++);
    }

    public static abstract class RegisteredListener implements Comparable<RegisteredListener> {

        private final Class<?> eventType;
        private final boolean receiveCancelled;
        private final Order order;

        public RegisteredListener(Class<?> eventType, boolean receiveCancelled, Order order) {
            this.eventType = eventType;
            this.receiveCancelled = receiveCancelled;
            this.order = order;
        }

        abstract public Object getOwner();

        public Class<?> getEventType() {
            return eventType;
        }

        public boolean isReceiveCancelled() {
            return receiveCancelled;
        }

        public Order getOrder() {
            return order;
        }

        abstract public void invoke(Event event);

        @Override
        public int compareTo(RegisteredListener o) {
            return getOrder().ordinal() - o.getOrder().ordinal();
        }
    }

}
