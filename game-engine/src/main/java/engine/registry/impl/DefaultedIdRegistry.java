package engine.registry.impl;

import engine.registry.Name;
import engine.registry.Registrable;

import javax.annotation.Nonnull;

public class DefaultedIdRegistry<T extends Registrable<T>> extends IdRegistry<T> {
    protected final Name defaultName;
    protected T defaultValue;
    protected int defaultId = -1;

    public DefaultedIdRegistry(Class<T> entryType, Name defaultName) {
        super(entryType);
        this.defaultName = defaultName;
    }

    public DefaultedIdRegistry(Class<T> entryType, String name, Name defaultName) {
        super(entryType, name);
        this.defaultName = defaultName;
    }

    public Name getDefaultName() {
        return defaultName;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public int getDefaultId() {
        return defaultId;
    }

    @Nonnull
    @Override
    public T register(@Nonnull T obj) {
        super.register(obj);

        if (defaultName.equals(obj.getName())) {
            defaultValue = obj;
        }

        return obj;
    }

    @Override
    public T getValue(String key) {
        return nameToObject.getOrDefault(key, defaultValue);
    }

    @Override
    public int getId(T obj) {
        return obj != null ? super.getId(obj) : defaultId;
    }

    @Override
    public T getValue(int id) {
        if (id >= idToObject.length) return defaultValue;
        T value = idToObject[id];
        return value != null ? value : defaultValue;
    }

    @Override
    protected void setIdUnsafe(T entry, int id) {
        super.setIdUnsafe(entry, id);

        if (defaultName.equals(entry.getName())) {
            defaultId = id;
        }
    }
}
