package com.github.unknownstudio.unknowndomain.engineapi.registry;

import com.github.unknownstudio.unknowndomain.engineapi.resource.ResourceLocation;

public interface IdentifiedRegistry<T extends RegistryEntry<T>> extends Registry<T>{

	int getId(T obj);
	
	int getId(ResourceLocation key);
}
