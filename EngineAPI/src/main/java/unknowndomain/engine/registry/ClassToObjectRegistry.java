package unknowndomain.engine.registry;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface ClassToObjectRegistry<T extends RegistryEntry<T>> extends Registry<T> {

    T getValue(Class<? extends T> clazz);

    Class<? extends T> getClassKey(T value);

    boolean containsKey(Class<? extends T> key);

    Set<Class<? extends T>> getClassKeys();

    Collection<Map.Entry<Class<? extends T>, T>> getC2OEntries();
}
