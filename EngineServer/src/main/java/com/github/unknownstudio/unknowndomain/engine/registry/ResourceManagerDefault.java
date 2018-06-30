package com.github.unknownstudio.unknowndomain.engine.registry;

import com.github.unknownstudio.unknowndomain.engineapi.Platform;
import com.github.unknownstudio.unknowndomain.engineapi.registry.IResourcesManager;
import com.github.unknownstudio.unknowndomain.engineapi.registry.RegistryEntry;
import com.github.unknownstudio.unknowndomain.engineapi.resource.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ResourceManagerDefault<T extends RegistryEntry<T>> implements IResourcesManager<T> {

    private final Map<ResourceLocation, T> registries;

    public ResourceManagerDefault(){
        registries = new HashMap<>();
    }

    @Override
    public List<T> getAllEntries() {
        return new ArrayList<>(registries.values());
    }

    @Override
    public T getEntry(ResourceLocation location) {
        return registries.get(location);
    }

    @Override
    public T register(T obj) {
        ResourceLocation location = obj.getRegistryName();
        if(registries.containsKey(location)){
            Platform.getServerLogger().warn("Resource location {} is registered already! Registered entry: {} registering entry: ", location, registries.get(location), obj);
            return registries.get(location);
        }
        registries.put(location, obj);
        return obj;
    }
}
