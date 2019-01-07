package unknowndomain.engine.registry;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import org.apache.commons.lang3.Validate;

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
    protected final Map<String, Integer> nameToId = HashBiMap.create();

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

        nameToObject.put(getUniqueName(obj), obj);
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
        String regId = obj.getUniqueName();
        return nameToId.get(regId);
    }

    @Override
    public int getId(String key) {
        Validate.notNull(key);
        return nameToId.get(key);
    }

    @Override
    public String getKey(int id) {
        return idToObject.get(id).getLocalName();
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

    protected String getUniqueName(RegistryEntry<T> entry) {
        // FIXME: Support mod
        String uniqueName = "unknowndomain." + name + "." + entry.getLocalName();
        setUniqueName(entry, uniqueName);
        return uniqueName;
    }

    private Field uniqueNameField;
    private Field idField;

    protected void setUniqueName(RegistryEntry<T> entry, String uniqueName) {
        if (uniqueNameField == null) {
            try {
                uniqueNameField = RegistryEntry.Impl.class.getField("uniqueName");
                uniqueNameField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                throw new RegisterException("Cannot init unique name.", e);
            }
        }
        try {
            uniqueNameField.set(entry, uniqueName);
        } catch (IllegalAccessException e) {
            throw new RegisterException("Cannot init unique name.", e);
        }
    }

    protected void setId(RegistryEntry<T> entry, int id) {
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
        } catch (IllegalAccessException e) {
            throw new RegisterException("Cannot init id.", e);
        }
    }
}
