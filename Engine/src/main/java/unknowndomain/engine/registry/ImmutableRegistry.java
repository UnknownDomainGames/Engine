package unknowndomain.engine.registry;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.apache.commons.lang3.Validate;

class ImmtableRegistry<T extends RegistryEntry<T>> implements IdentifiedRegistry<T> {
    private final Class<T> registryType;
    private final ImmutableMap<String, T> nameToObject;
    private final ImmutableMap<String, Integer> nameToId;
    private final ImmutableList<T> idToObject;

    static <T extends RegistryEntry<T>> ImmtableRegistry<T> synchronize(IdentifiedRegistry<T> registry,
            Map<String, Integer> mapping) {
        Collection<T> values = registry.getValues();
        return null;
        // return new ImmtableRegistry<T>(registry.getClass(),
        // ImmutableMap.builder().put(entry));
    }

    ImmtableRegistry(Class<T> registryType, ImmutableMap<String, T> nameToObject,
            ImmutableMap<String, Integer> nameToId, ImmutableList<T> idToObject) {
        this.registryType = registryType;
        this.nameToObject = nameToObject;
        this.nameToId = nameToId;
        this.idToObject = idToObject;
    }

    @Override
    public Class<T> getRegistryEntryType() {
        return registryType;
    }

    @Override
    public T register(T obj) throws RegisterException {
        throw new RegisterException();
    }

    @Override
    public T getValue(String registryName) {
        Validate.notNull(registryName);
        return nameToObject.get(registryName);
    }

    @Override
    public String getKey(T value) {
        Validate.notNull(value);
        return value.getRegistryName();
    }

    @Override
    public boolean containsKey(String key) {
        Validate.notNull(key);
        return nameToObject.containsKey(key);
    }

    @Override
    public boolean containsValue(T value) {
        Validate.notNull(value);
        return nameToObject.containsKey(value.getRegistryName());
    }

    @Override
    public Set<String> getKeys() {
        return nameToObject.keySet();
    }

    @Override
    public Collection<T> getValues() {
        return idToObject;
    }

    @Override
    public int getId(T obj) {
        Validate.notNull(obj);
        return nameToId.get(obj.getRegistryName());
    }

    @Override
    public int getId(String key) {
        Validate.notNull(key);
        return nameToId.get(key);
    }

    @Override
    public String getKey(int id) {
        return idToObject.get(id).getRegistryName();
    }

    @Override
    public T getValue(int id) {
        return idToObject.get(id);
    }
}