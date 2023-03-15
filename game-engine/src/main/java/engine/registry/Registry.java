package engine.registry;

import engine.util.Validate;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

/**
 * The registry of the a type of object
 *
 * @param <T> The managed type
 */
public interface Registry<T extends Registrable<T>> extends Iterable<T> {

    @Nonnull
    Class<T> getEntryType();

    @Nonnull
    Name getRegistryName();

    @Nonnull
    T register(@Nonnull T obj) throws RegistrationException;

    @SuppressWarnings("unchecked") // for varargs heap pollution
    default void registerAll(@Nonnull T... objects) throws RegistrationException {
        for (T object : objects) {
            Validate.notNull(object);
            if (!getEntryType().isInstance(object)) {
                throw new RegistrationException("Registry required " + getEntryType().getName()
                        + " but got target object(Class: " + object.getClass().getName() + ", ToString:" + object + ")");
            }
            register(object);
        }
    }

    boolean containsValue(T value);

    /**
     * Get the registered object by the unique name of the object.
     *
     * @param key The unique name of the object
     * @return The object
     * @see Registrable#getName()
     */
    T getValue(String key);

    T getValue(Name key);

    boolean containsKey(String key);

    boolean containsKey(Name key);

    Collection<T> getValues();

    Set<String> getKeys();

    int getId(T obj);

    int getId(String key);

    int getId(Name key);

    Name getKey(int id);

    T getValue(int id);

    Collection<Entry<String, T>> getEntries();
}
