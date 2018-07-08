package unknowndomain.engine.api.registry;

import unknowndomain.engine.api.resource.ResourceLocation;

public interface IdentifiedRegistry<T extends RegistryEntry<T>> extends Registry<T>{

	int getId(T obj);
	
	int getId(ResourceLocation key);
}
