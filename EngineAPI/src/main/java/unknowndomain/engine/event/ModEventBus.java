package unknowndomain.engine.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Event bus for loading mods
 *
 */
public class ModEventBus implements EventBus {
    private List<Object> modContainers;

    public ModEventBus() {
        modContainers = new ArrayList<>();
    }

    @Override
    public boolean post(Event event) {
        /*
         * This is designed to be called infrequently, namely once during loading and
         * once during unloading (if there is unloading)
         */
        for (Object mod : modContainers) {
            Class<?> clazz = mod.getClass();
            Method[] methods = clazz.getDeclaredMethods();
            for (Method m : methods) {
                m.setAccessible(true);
                // must listen to event
                if (m.getAnnotation(ModEventListener.class) == null)
                    continue;

                // must accept 1 event
                if (m.getParameterCount() != 1)
                    continue;

                // must accept the correct event type
                if (m.getParameterTypes()[0] != event.getClass())
                    continue;
                try {
                    m.invoke(mod, event);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

        }
        // not cancellable
        return false;
    }

    @Override
    public void register(Object listener) {
        modContainers.add(listener);
    }

    @Override
    public void unregister(Object listener) {
        modContainers.remove(listener);
    }

}
