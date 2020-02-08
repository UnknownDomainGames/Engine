package engine.registry.impl;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import engine.registry.Name;
import engine.registry.Registrable;
import engine.registry.RegistrationException;
import engine.registry.Registry;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.requireNonNull;

@NotThreadSafe
public class SimpleRegistry<T extends Registrable<T>> implements Registry<T> {

    private final Class<T> entryType;
    private final String name;

    protected final BiMap<String, T> nameToObject = HashBiMap.create();
    protected final IntObjectMap<T> idToObject = new IntObjectHashMap<>();

    public SimpleRegistry(Class<T> entryType) {
        this(entryType, entryType.getSimpleName().toLowerCase());
    }

    public SimpleRegistry(Class<T> entryType, String name) {
        this.entryType = entryType;
        this.name = name;
    }

    @Nonnull
    @Override
    public T register(@Nonnull T obj) {
        requireNonNull(obj);

        if (!(obj instanceof Registrable.Impl)) {
            throw new RegistrationException(String.format("%s must be a subclass of RegistryEntry.Impl", obj.getEntryType().getSimpleName()));
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
        return obj == null ? -1 : obj.getId();
    }

    @Override
    public int getId(String key) {
        return getId(getValue(key));
    }

    @Override
    public int getId(Name key) {
        return getId(key.toString());
    }

    @Override
    public Name getKey(int id) {
        return idToObject.get(id).getName();
    }

    @Override
    public T getValue(int id) {
        return idToObject.get(id);
    }

    @Override
    public Collection<Map.Entry<String, T>> getEntries() {
        return nameToObject.entrySet();
    }

    private static Field idField;

    protected void setId(T entry, int id) {
        if (idField == null) {
            try {
                idField = Registrable.Impl.class.getDeclaredField("id");
                idField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                throw new RegistrationException("Cannot init id.", e);
            }
        }
        try {
            idField.setInt(entry, id);
            idToObject.put(id, entry);
        } catch (IllegalAccessException e) {
            throw new RegistrationException("Cannot init id.", e);
        }
    }
}
