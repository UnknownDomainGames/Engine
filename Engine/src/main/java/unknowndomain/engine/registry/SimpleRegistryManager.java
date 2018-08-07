package unknowndomain.engine.registry;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.Validate;

public class SimpleRegistryManager implements RegistryManager {
	
	private Map<Class<?>, Registry<?>> registries = new HashMap<>();
	
	@Override
	public <T extends RegistryEntry<T>> Registry<T> getRegistry(@Nonnull Class<T> type) {
		@SuppressWarnings("unchecked")
		Registry<T> registry = (Registry<T>) registries.get(Validate.notNull(type));
		if (registry == null) { 
			registry = new SimpleIdentifiedRegistry<T>(){}; // TODO: here we need to consider how to build registry
			registries.put(type, registry);
		}
		return registry;
	}

	@Override
	public <T extends RegistryEntry<T>> boolean hasRegistry(Class<T> type) {
		return registries.containsKey(type);
	}
	
	@Override
	public <T extends RegistryEntry<T>> void addRegistry(Class<T> type, Registry<T> registry) {
		Validate.notNull(type);
		Validate.notNull(registry);
		if (registries.containsKey(type)) {
			throw new RegistryException(String.format("Registry<%s> has been existed.", type.getName()));
		} 
		registries.put(type, registry);
	}
	
	@Override
	public Collection<Entry<Class<?>, Registry<?>>> getEntries() {
		return registries.entrySet();
	}

	@Override
	public <T extends RegistryEntry<T>> void register(T obj) {
		Validate.notNull(obj); 
		getRegistry(obj.getRegistryType()).register(obj);
	}
}
