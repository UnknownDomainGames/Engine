package unknowndomain.engine.api.registry;

public interface RegistryManager {

	<T extends RegistryEntry<T>> Registry<T> getRegistry(Class<T> type);
	
	<T extends RegistryEntry<T>> boolean hasRegistry(Class<T> type);
	
	<T extends RegistryEntry<T>> void registry(T obj);
}
