package unknowndomain.engine.api.registry;

import unknowndomain.engine.api.util.DomainedPath;

public interface IdentifiedRegistry<T extends RegistryEntry<T>> extends Registry<T>{

	int getId(T obj);
	
	int getId(DomainedPath key);
	
	DomainedPath getKey(int id);
	
	T getValue(int id);
}
