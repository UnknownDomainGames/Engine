package unknowndomain.engine.registry;

import unknowndomain.engine.client.resource.ResourcePath;

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

    T getValue(ResourcePath registryName);

	default T getValue(String registryName) {
        return getValue(new ResourcePath(registryName));
	}

    ResourcePath getKey(T value);

    boolean containsKey(ResourcePath key);

	boolean containsValue(T value);

    Set<ResourcePath> getKeys();

	Collection<T> getValues();

//    Collection<Entry<ResourcePath, T>> getEntries();
}
