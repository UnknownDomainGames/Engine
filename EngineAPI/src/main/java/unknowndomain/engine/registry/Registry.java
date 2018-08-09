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

    T getValue(String registryName);

	// default T getValue(String registryName) {
    //     return getValue(new ResourcePath(registryName));
	// }

    String getKey(T value);

    boolean containsKey(String key);

	boolean containsValue(T value);

    Set<String> getKeys();

	Collection<T> getValues();

//    Collection<Entry<ResourcePath, T>> getEntries();
}
