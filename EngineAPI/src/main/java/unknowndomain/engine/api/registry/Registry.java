package unknowndomain.engine.api.registry;

import java.util.Map.Entry;

import unknowndomain.engine.api.util.DomainedPath;

import java.util.Set;

public interface Registry<T extends RegistryEntry<T>> {

	Class<T> getRegistryEntryType();

	T register(T obj) throws RegisterException;

	default void registerAll(T... objs) throws RegisterException {
		for (T obj : objs)
			register(obj);
	}

	T getValue(DomainedPath registryName);

	default T getValue(String registryName) {
		return getValue(new DomainedPath(registryName));
	}
	
	DomainedPath getKey(T value);

	boolean containsKey(DomainedPath key);

	boolean containsValue(T value);

	Set<DomainedPath> getKeys();
	
	Set<T> getValues();
	
	Set<Entry<DomainedPath, T>> getEntries();
}
