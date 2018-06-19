package com.github.unknownstudio.unknowndomain.engineapi.registry;

import com.github.unknownstudio.unknowndomain.engineapi.resource.ResourceLocation;

public interface RegistryEntry<T> {

	ResourceLocation getRegistryName();

	String getRegistryNameString();

	T setRegistryName(ResourceLocation location);

	default T setRegistryName(String name){
		return setRegistryName(new ResourceLocation(name));
	}
}
