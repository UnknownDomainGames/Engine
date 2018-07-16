package unknowndomain.engine.registry;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.Validate;

import unknowndomain.engine.api.registry.Registry;
import unknowndomain.engine.api.registry.RegistryEntry;
import unknowndomain.engine.api.registry.RegistryManager;
import unknowndomain.engine.api.registry.SimpleRegistry;

public class SimpleRegistryManager implements RegistryManager {
	
	private Map<Class<?>, Registry<?>> registries = new HashMap<>();

	@Override
	public <T extends RegistryEntry<T>> Registry<T> getRegistry(@Nonnull Class<T> type) {
		@SuppressWarnings("unchecked")
		Registry<T> registry = (Registry<T>) registries.get(Validate.notNull(type));
		if (registry == null) {
			registry = new SimpleRegistry<T>(){};
			registries.put(type, registry);
		}
		return registry;
	}

	@Override
	public <T extends RegistryEntry<T>> boolean hasRegistry(Class<T> type) {
		return registries.containsKey(type);
	}

	@Override
	public <T extends RegistryEntry<T>> void register(T obj) {
		getRegistry(obj.getRegistryType()).register(obj);
	}

}
