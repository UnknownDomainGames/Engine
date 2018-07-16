package unknowndomain.engine.api.registry;

import java.util.Map.Entry;
import java.util.Set;

public interface Registry<T extends RegistryEntry<T>> {

	Class<T> getRegistryEntryType();

	T register(T obj) throws RegisterException;

	default void registerAll(T... objs) throws RegisterException {
		for (T obj : objs)
			register(obj);
	}

	T getValue(RegistryName registryName);

	default T getValue(String registryName) {
		return getValue(new RegistryName(registryName));
	}
	
	RegistryName getKey(T value);

	boolean containsKey(RegistryName key);

	boolean containsValue(T value);

	Set<RegistryName> getKeys();
	
	Set<T> getValues();
	
	Set<Entry<RegistryName, T>> getEntries();
}
