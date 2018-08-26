package unknowndomain.engine.registry;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import org.apache.commons.lang3.Validate;
import unknowndomain.engine.mod.ModContainer;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class MutableRegistry<T extends RegistryEntry<T>> implements Registry<T> {
    private final Class<T> registryType;
    private final String name;

    private final Map<String, T> nameToObject = Maps.newHashMap();
    private IntObjectMap<T> idToObject = new IntObjectHashMap<>();
    private Map<String, Integer> nameToId = HashBiMap.create();

    private AtomicInteger id = new AtomicInteger();

    private ModContainer activeMod = null;

    MutableRegistry(Class<T> registryType) {
        this.registryType = registryType;
        this.name = registryType.getSimpleName().toLowerCase();
    }

    public MutableRegistry(Class<T> registryType, String name) {
        this.registryType = registryType;
        this.name = name;
    }

    public void setActiveMod(ModContainer activeMod) {
        this.activeMod = activeMod;
    }

    @Override
    public T register(T obj) {
        Validate.notNull(obj);

        ((Impl) obj).setup(activeMod, this);

        String regId = obj.getUniqueName();

        int next = id.getAndIncrement();
        nameToObject.put(regId, obj);
        idToObject.put(next, obj);
        nameToId.put(regId, next);
        return obj;
    }

    @Override
    public Class<T> getRegistryEntryType() {
        return registryType;
    }

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
}
