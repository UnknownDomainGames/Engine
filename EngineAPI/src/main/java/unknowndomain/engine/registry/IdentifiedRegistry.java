package unknowndomain.engine.registry;

import unknowndomain.engine.client.resource.ResourcePath;

import java.util.Map.Entry;
import java.util.Collection;

public interface IdentifiedRegistry<T extends RegistryEntry<T>> extends Registry<T> {
    int getId(T obj);

    int getId(ResourcePath key);

    ResourcePath getKey(int id);

    T getValue(int id);

    Collection<Entry<ResourcePath, Integer>> getNameToId();
}
