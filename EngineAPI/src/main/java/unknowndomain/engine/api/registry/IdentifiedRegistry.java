package unknowndomain.engine.api.registry;

public interface IdentifiedRegistry<T extends RegistryEntry<T>> extends Registry<T>{

	int getId(T obj);
	
	int getId(RegistryName key);
	
	RegistryName getKey(int id);
	
	T getValue(int id);
}
