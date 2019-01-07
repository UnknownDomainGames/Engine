package unknowndomain.engine.registry.impl;

import unknowndomain.engine.registry.RegistryEntry;

import java.util.concurrent.atomic.AtomicInteger;

public class IdAutoIncreaseRegistry<T extends RegistryEntry<T>> extends SimpleRegistry<T> {

    private final AtomicInteger nextId = new AtomicInteger(0);

    public IdAutoIncreaseRegistry(Class entryType) {
        super(entryType);
    }

    public IdAutoIncreaseRegistry(Class entryType, String name) {
        super(entryType, name);
    }

    @Override
    public T register(T obj) {
        super.register(obj);

        int id = nextId.getAndIncrement();
        idToObject.put(id, obj);
        setId(obj, id);

        return obj;
    }
}
