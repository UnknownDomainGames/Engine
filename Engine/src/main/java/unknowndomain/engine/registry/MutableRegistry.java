package unknowndomain.engine.registry;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import org.apache.commons.lang3.Validate;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

class MutableRegistry<T extends RegistryEntry<T>> implements Registry<T> {
    private final Class<T> registryType;
    private final String name;

    private final Map<String, T> nameToObject = Maps.newHashMap();
    private IntObjectMap<T> idToObject = new IntObjectHashMap<>();
    private Map<String, Integer> nameToId = HashBiMap.create();

    private AtomicInteger id = new AtomicInteger();

    private String activeMod;

    MutableRegistry(Class<T> registryType) {
        this.registryType = registryType;
        this.name = registryType.getSimpleName().toLowerCase();
    }

    MutableRegistry(Class<T> registryType, String name) {
        this.registryType = registryType;
        this.name = name;
    }

    public void setActiveMod(String activeMod) {
        this.activeMod = activeMod;
    }

    @Override
    public T register(T obj) {
        Validate.notNull(obj);
        obj.setRegistryName(String.join(".", activeMod, name, obj.getRegisteredName()));
        int next = id.getAndIncrement();
        nameToObject.put(obj.getRegisteredName(), obj);
        idToObject.put(next, obj);
        nameToId.put(obj.getRegisteredName(), next);
        return obj;
    }

    @Override
    public Class<T> getRegistryEntryType() {
        return registryType;
    }

    @Override
    public T getValue(String registryName) {
        Validate.notNull(registryName);
        return nameToObject.get(registryName);
    }

    @Override
    public String getKey(T value) {
        Validate.notNull(value);
        return value.getRegisteredName();
    }

    @Override
    public boolean containsKey(String key) {
        Validate.notNull(key);
        return nameToObject.containsKey(key);
    }

    @Override
    public boolean containsValue(T value) {
        Validate.notNull(value);
        return nameToObject.containsKey(value.getRegisteredName());
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
        return nameToId.get(obj.getRegisteredName());
    }

    @Override
    public int getId(String key) {
        Validate.notNull(key);
        return nameToId.get(key);
    }

    @Override
    public String getKey(int id) {
        return idToObject.get(id).getRegisteredName();
    }

    @Override
    public T getValue(int id) {
        return idToObject.get(id);
    }

    @Override
    public Collection<Map.Entry<String, T>> getEntries() {
        return nameToObject.entrySet();
    }
}
