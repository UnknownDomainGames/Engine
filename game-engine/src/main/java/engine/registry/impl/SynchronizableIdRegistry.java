package engine.registry.impl;

import engine.registry.Registrable;
import engine.registry.SynchronizableRegistry;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

public class SynchronizableIdRegistry<T extends Registrable<T>> extends IdAutoIncreaseRegistry<T> implements SynchronizableRegistry<T> {
    public SynchronizableIdRegistry(Class<T> entryType) {
        super(entryType);
    }

    public SynchronizableIdRegistry(Class<T> entryType, String name) {
        super(entryType, name);
    }

    private final Object2IntMap<String> syncedMapping = new Object2IntOpenHashMap<>();

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
    public void sync(String key, int id) {
        syncedMapping.put(key, id);
    }

    @Override
    public void unsync() {
        syncedMapping.clear();
    }
}
