package unknowndomain.game.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import unknowndomain.engine.registry.ClassToObjectRegistry;
import unknowndomain.engine.registry.RegisterException;
import unknowndomain.engine.registry.RegistryEntry;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public abstract class SimpleClassToObjectRegistry<T extends RegistryEntry<T>> implements ClassToObjectRegistry<T> {

    protected final BiMap<Class<? extends T>, T> clazzToObject = HashBiMap.create();

    @Nonnull
    @Override
    public T register(@Nonnull T obj) throws RegisterException {
        if(clazzToObject.containsKey(obj.getClass())){
            throw new RegisterException(String.format("Class %s has already registered!", obj.getClass().getName()));
        }
        clazzToObject.put((Class<? extends T>) obj.getClass(), obj);
        return obj;
    }

    @Override
    public boolean containsValue(T value) {
        return clazzToObject.containsValue(value);
    }

    @Override
    public T getValue(Class<? extends T> clazz) {
        return clazzToObject.get(clazz);
    }

    @Override
    public T getValue(String registryName) {
        return null;
    }

    @Override
    public Class<? extends T> getClassKey(T value) {
        return clazzToObject.inverse().get(value);
    }

    @Override
    public String getKey(T value) {
        return null;
    }

    @Override
    public boolean containsKey(Class<? extends T> key) {
        return clazzToObject.containsKey(key);
    }

    @Override
    public boolean containsKey(String key) {
        return false;
    }

    @Override
    public Collection<T> getValues() {
        return clazzToObject.values();
    }

    @Override
    public Set<Class<? extends T>> getClassKeys() {
        return clazzToObject.keySet();
    }

    @Override
    public int getId(T obj) {
        return 0;
    }

    @Override
    public int getId(String key) {
        return 0;
    }

    @Override
    public String getKey(int id) {
        return null;
    }

    @Override
    public T getValue(int id) {
        return null;
    }

    @Override
    public Collection<Map.Entry<Class<? extends T>, T>> getC2OEntries() {
        return clazzToObject.entrySet();
    }

    @Override
    public Collection<Map.Entry<String, T>> getEntries() {
        return null;
    }
}
