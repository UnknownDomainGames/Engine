package nullengine.registry.impl;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import nullengine.registry.ClassToObjectRegistry;
import nullengine.registry.Registrable;
import nullengine.registry.RegistrationException;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public abstract class SimpleClassToObjectRegistry<T extends Registrable<T>> extends SimpleRegistry<T> implements ClassToObjectRegistry<T> {

    protected final BiMap<Class<? extends T>, T> clazzToObject = HashBiMap.create();

    public SimpleClassToObjectRegistry(Class<T> entryType) {
        super(entryType);
    }

    public SimpleClassToObjectRegistry(Class<T> entryType, String name) {
        super(entryType, name);
    }

    @Nonnull
    @Override
    public T register(@Nonnull T obj) throws RegistrationException {
        if (clazzToObject.containsKey(obj.getClass())) {
            throw new RegistrationException(String.format("Class %s has already registered!", obj.getClass().getName()));
        }
        super.register(obj);
        clazzToObject.put((Class<? extends T>) obj.getClass(), obj);
        return obj;
    }

    @Override
    public T getValue(Class<? extends T> clazz) {
        return clazzToObject.get(clazz);
    }

    @Override
    public Class<? extends T> getClassKey(T value) {
        return clazzToObject.inverse().get(value);
    }

    @Override
    public boolean containsKey(Class<? extends T> key) {
        return clazzToObject.containsKey(key);
    }

    @Override
    public Set<Class<? extends T>> getClassKeys() {
        return clazzToObject.keySet();
    }

    @Override
    public Collection<Map.Entry<Class<? extends T>, T>> getC2OEntries() {
        return clazzToObject.entrySet();
    }

}
