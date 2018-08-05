package unknowndomain.engine.registry;

import unknowndomain.engine.client.resource.ResourcePath;

public interface IdentifiedRegistry<T extends RegistryEntry<T>> extends Registry<T> {
    int getId(T obj);

    int getId(ResourcePath key);

    ResourcePath getKey(int id);

    T getValue(int id);
}
