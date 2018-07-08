package unknowndomain.engine.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import unknowndomain.engine.api.Platform;
import unknowndomain.engine.api.registry.RegistryEntry;
import unknowndomain.engine.api.registry.ResourcesManager;
import unknowndomain.engine.api.resource.ResourceLocation;

public class ResourceManagerDefault<T extends RegistryEntry<T>> implements ResourcesManager<T> {

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
