package unknowndomain.engine.registry.impl;

import com.google.common.collect.Maps;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import org.apache.commons.lang3.Validate;
import unknowndomain.engine.registry.RegisterException;
import unknowndomain.engine.registry.Registry;
import unknowndomain.engine.registry.RegistryEntry;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class SimpleRegistry<T extends RegistryEntry<T>> implements Registry<T> {

    private final Class<T> entryType;
    private final String name;

    protected final Map<String, T> nameToObject = Maps.newHashMap();
    protected final IntObjectMap<T> idToObject = new IntObjectHashMap<>();

    private boolean freezed = false;

    public SimpleRegistry(Class<T> entryType) {
        this(entryType, entryType.getSimpleName().toLowerCase());
    }

    public SimpleRegistry(Class<T> entryType, String name) {
        this.entryType = entryType;
        this.name = name;
    }

    @Override
    public T register(T obj) {
        Validate.notNull(obj);

        if (freezed) {
            throw new RegisterException("Registry has been freezed.");
        }

        if (!(obj instanceof RegistryEntry.Impl)) {
            throw new RegisterException(String.format("%s must be a subclass of RegistryEntry.Impl", obj.getEntryType().getSimpleName()));
        }

        setUniqueName(obj, getUniqueName(obj));
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
    public T getValue(String registryName) {
        Validate.notNull(registryName);
        return nameToObject.get(registryName);
    }

    @Override
    public String getKey(T value) {
        Validate.notNull(value);
        return value.getUniqueName();
    }

    @Override
    public boolean containsKey(String key) {
        Validate.notNull(key);
        return nameToObject.containsKey(key);
    }

    @Override
    public boolean containsValue(T value) {
        Validate.notNull(value);
        String regId = value.getUniqueName();
        return nameToObject.containsKey(regId);
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
        Validate.notNull(obj);
        return obj.getId();
    }

    @Override
    public int getId(String key) {
        Validate.notNull(key);
        return getValue(key).getId();
    }

    @Override
    public String getKey(int id) {
        return idToObject.get(id).getUniqueName();
    }

    @Override
    public T getValue(int id) {
        return idToObject.get(id);
    }

    @Override
    public Collection<Map.Entry<String, T>> getEntries() {
        return nameToObject.entrySet();
    }

    @Override
    public void freeze() {
        freezed = true;
    }

    protected String getUniqueName(T entry) {
        // FIXME: Support mod
        return "unknowndomain." + name + "." + entry.getLocalName();
    }

    private static Field uniqueNameField;
    private static Field idField;

    protected void setUniqueName(T entry, String uniqueName) {
        if (uniqueNameField == null) {
            try {
                uniqueNameField = RegistryEntry.Impl.class.getDeclaredField("uniqueName");
                uniqueNameField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                throw new RegisterException("Cannot init unique name.", e);
            }
        }
        try {
            uniqueNameField.set(entry, uniqueName);
            nameToObject.put(uniqueName, entry);
        } catch (IllegalAccessException e) {
            throw new RegisterException("Cannot set unique name.", e);
        }
    }

    protected void setId(T entry, int id) {
        if (idField == null) {
            try {
                idField = RegistryEntry.Impl.class.getField("id");
                idField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                throw new RegisterException("Cannot init id.", e);
            }
        }
        try {
            idField.setInt(entry, id);
            idToObject.put(id, entry);
        } catch (IllegalAccessException e) {
            throw new RegisterException("Cannot init id.", e);
        }
    }
}
