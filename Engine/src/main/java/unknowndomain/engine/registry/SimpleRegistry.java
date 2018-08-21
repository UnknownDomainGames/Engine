package unknowndomain.engine.registry;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;

public class SimpleRegistry<T extends RegistryEntry<T>> implements Registry<T> {
    private final Map<String, T> registeredItems = Maps.newHashMap();
    @SuppressWarnings("serial")
    private final TypeToken<T> token = new TypeToken<T>(getClass()) {
    };

    public SimpleRegistry() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<T> getRegistryEntryType() {
        return (Class<T>) token.getRawType();
    }

    @Override
    public T register(T obj) {
        String key = obj.getRegistryName();
        if (registeredItems.containsKey(key))
            throw new RegisterException("\"" + key + "\" has been registered.");

        registeredItems.put(key, obj);
        return obj;
    }

    @Override
    public T getValue(String key) {
        return registeredItems.get(key);
    }

    @Override
    public String getKey(T value) {
        return value.getRegistryName();
    }

    @Override
    public boolean containsKey(String key) {
        return registeredItems.containsKey(key);
    }

    @Override
    public boolean containsValue(T value) {
        return registeredItems.containsValue(value);
    }

    @Override
    public Set<String> getKeys() {
        return registeredItems.keySet();
    }

    @Override
    public Collection<T> getValues() {
        return registeredItems.values();
    }

//	@Override
//    public Collection<Entry<ResourcePath, T>> getEntries() {
//		return registeredItems.entrySet();
//	}
}
