package engine.registry.impl;

import engine.registry.Name;
import engine.registry.Registrable;
import engine.registry.RegistrationException;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

@NotThreadSafe
public abstract class IdRegistry<T extends Registrable<T>> extends NonIdRegistry<T> {

    protected final IntObjectMap<T> idToObject = new IntObjectHashMap<>();

    public IdRegistry(Class<T> entryType) {
        this(entryType, entryType.getSimpleName().toLowerCase());
    }

    public IdRegistry(Class<T> entryType, String name) {
        super(entryType, name);
    }

    @Nonnull
    @Override
    public T register(@Nonnull T obj) {
        if (!(obj instanceof Registrable.Impl)) {
            throw new RegistrationException(String.format("%s must be a subclass of RegistryEntry.Impl", obj.getEntryType().getSimpleName()));
        }

        return super.register(obj);
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
