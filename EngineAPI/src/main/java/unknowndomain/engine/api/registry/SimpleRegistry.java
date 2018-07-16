package unknowndomain.engine.api.registry;

import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.reflect.TypeToken;

public class SimpleRegistry<T extends RegistryEntry<T>> implements Registry<T> {
	
	private final BiMap<RegistryName, T> registeredItems = HashBiMap.create();
	@SuppressWarnings("serial")
	private final TypeToken<T> token = new TypeToken<T>(getClass()) {};
	
	public SimpleRegistry() {
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Class<T> getRegistryEntryType() {
		return (Class<T>) token.getRawType();
	}

	@Override
	public T register(T obj) {
		RegistryName key = obj.getRegistryName();
		if(registeredItems.containsKey(key))
			throw new RegisterException("\""+key+"\" has been registered.");
		
		registeredItems.put(key, obj);
		return obj;
	}

	@Override
	public T getValue(RegistryName key) {
		return registeredItems.get(key);
	}

	@Override
	public RegistryName getKey(T value) {
		return registeredItems.inverse().get(value);
	}

	@Override
	public boolean containsKey(RegistryName key) {
		return registeredItems.containsKey(key);
	}

	@Override
	public boolean containsValue(T value) {
		return registeredItems.containsValue(value);
	}

	@Override
	public Set<RegistryName> getKeys() {
		return registeredItems.keySet();
	}
	
	@Override
	public Set<T> getValues() {
		return registeredItems.values();
	}

	@Override
	public Set<Entry<RegistryName, T>> getEntries() {
		return registeredItems.entrySet();
	}
}
