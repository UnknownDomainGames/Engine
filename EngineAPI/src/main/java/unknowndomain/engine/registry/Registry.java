package unknowndomain.engine.registry;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

public interface Registry<T extends RegistryEntry<T>> {
    Class<T> getRegistryEntryType();

    T register(T obj) throws RegisterException;

    default void registerAll(T... objs) throws RegisterException {
        for (T obj : objs)
            register(obj);
    }

    boolean containsValue(T value);

    T getValue(String registryName);

    // default T getValue(String registryName) {
    //     return getValue(new ResourcePath(registryName));
    // }

    String getKey(T value);

    boolean containsKey(String key);

    Collection<T> getValues();

    Set<String> getKeys();

    int getId(T obj);

    int getId(String key);

    String getKey(int id);

    T getValue(int id);

    Collection<Entry<String, T>> getEntries();

    class Type<T extends RegistryEntry<T>> {
        public final String name;
        public final Class<T> type;

        private Type(String name, Class<T> type) {
            this.name = name;
            this.type = type;
        }

        public static <T extends RegistryEntry<T>> Type<T> of(String name, Class<T> type) {
            return new Type<>(name, type);
        }
    }
}
