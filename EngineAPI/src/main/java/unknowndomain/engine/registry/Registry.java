package unknowndomain.engine.registry;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

/**
 * The registry of the a type of object
 *
 * @param <T> The managed type
 */
public interface Registry<T extends RegistryEntry<T>> {
    Class<T> getRegistryEntryType();

    String getRegistryName();

    T register(RegistryEntry<T> obj) throws RegisterException;

    default void registerAll(RegistryEntry<T>... objs) throws RegisterException {
        for (RegistryEntry<T> obj : objs)
            register(obj);
    }

    boolean containsValue(RegistryEntry<T> value);

    /**
     * Get the registered object by the unique name of the object.
     *
     * @param registryName The unique name of the object
     * @return The object
     * @see RegistryEntry#getUniqueName()
     */
    T getValue(String registryName);

    /**
     * Well... this method is not really...
     *
     * @return The unique name of the object
     * @see RegistryEntry#getUniqueName()
     */
    String getKey(RegistryEntry<T> value);

    boolean containsKey(String key);

    Collection<T> getValues();

    Set<String> getKeys();

    int getId(RegistryEntry<T> obj);

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
