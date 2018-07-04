package com.github.unknownstudio.unknowndomain.engineapi.registry;

import java.util.Map.Entry;
import java.util.Set;

import com.github.unknownstudio.unknowndomain.engineapi.resource.ResourceLocation;

public interface Registry<T extends RegistryEntry<T>> {

	Class<T> getRegistryEntryType();

	T register(T obj) throws RegisterException;

	default void registerAll(T... objs) throws RegisterException {
		for (T obj : objs)
			register(obj);
	}

	T getValue(ResourceLocation registryName);

	default T getValue(String registryName) {
		return getValue(new ResourceLocation(registryName));
	}
	
	ResourceLocation getKey(T value);

	boolean containsKey(ResourceLocation key);

	boolean containsValue(T value);

	Set<ResourceLocation> getKeys();
	
	Set<T> getValues();
	
	Set<Entry<ResourceLocation, T>> getEntries();
}
