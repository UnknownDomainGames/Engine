package engine.registry.impl;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import engine.registry.Name;
import engine.registry.Registrable;
import engine.registry.Registry;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static org.apache.commons.lang3.Validate.notNull;

public class NonIdRegistry<T extends Registrable<T>> implements Registry<T> {
    private final Class<T> entryType;
    private final String name;

    protected final BiMap<String, T> nameToObject = HashBiMap.create();

    public NonIdRegistry(Class<T> entryType) {
        this(entryType, entryType.getSimpleName().toLowerCase());
    }

    public NonIdRegistry(Class<T> entryType, String name) {
        this.entryType = entryType;
        this.name = name;
    }

    @Nonnull
    @Override
    public T register(@Nonnull T obj) {
        notNull(obj, "Registrable object cannot be null");

        if (nameToObject.containsKey(obj.getName().toString())) {
            throw new IllegalStateException("Registrable object " + obj.getName() + " has been registered");
        }

        nameToObject.put(obj.getName().toString(), obj);
        return obj;
    }

    @Nonnull
    @Override
    public Class<T> getEntryType() {
        return entryType;
    }

    @Nonnull
    @Override
    public String getRegistryName() {
        return name;
    }

    @Override
    public T getValue(String key) {
        return nameToObject.get(key);
    }

    @Override
    public T getValue(Name key) {
        return getValue(key.toString());
    }

    @Override
    public boolean containsKey(String key) {
        return nameToObject.containsKey(key);
    }

    @Override
    public boolean containsKey(Name key) {
        return containsKey(key.toString());
    }

    @Override
    public boolean containsValue(T value) {
        return nameToObject.containsValue(value);
    }

    @Override
    public Set<String> getKeys() {
        return nameToObject.keySet();
    }

    @Override
    public Collection<T> getValues() {
        return nameToObject.values();
    }

    @Override
    public int getId(T obj) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getId(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getId(Name key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Name getKey(int id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T getValue(int id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Map.Entry<String, T>> getEntries() {
        return nameToObject.entrySet();
    }
}
