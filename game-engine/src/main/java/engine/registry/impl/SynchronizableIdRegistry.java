package engine.registry.impl;

import engine.registry.Registrable;
import engine.registry.SynchronizableRegistry;

import java.util.HashMap;
import java.util.Map;

public class SynchronizableIdRegistry<T extends Registrable<T>> extends IdAutoIncreaseRegistry<T> implements SynchronizableRegistry<T> {
    public SynchronizableIdRegistry(Class<T> entryType) {
        super(entryType);
    }

    public SynchronizableIdRegistry(Class<T> entryType, String name) {
        super(entryType, name);
    }

    private final Map<String, Integer> syncedMapping = new HashMap<>();

    @Override
    public int getId(T obj) {
        if (!syncedMapping.isEmpty()) {
            return syncedMapping.get(obj.getName().getUniqueName());
        }
        return getIntrinsicId(obj);
    }

    @Override
    public int getIntrinsicId(T obj) {
        return super.getId(obj);
    }

    @Override
    public void sync(Map<String, Integer> map) {
        if (getEntries().size() != map.size())
            throw new IllegalArgumentException("Sync map size does not match with size of registered objects");
        syncedMapping.putAll(map);
    }

    @Override
    public void unsync() {
        syncedMapping.clear();
    }
}
