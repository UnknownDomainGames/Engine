package nullengine.registry.impl;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import nullengine.entity.Entity;
import nullengine.entity.EntityProvider;
import nullengine.registry.RegistrationException;
import nullengine.registry.game.EntityRegistry;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class SimpleEntityRegistry extends IdAutoIncreaseRegistry<EntityProvider> implements EntityRegistry {

    protected final BiMap<Class<? extends Entity>, EntityProvider> clazzToObject = HashBiMap.create();

    public SimpleEntityRegistry() {
        super(EntityProvider.class, "entity");
    }

    @Override
    public <T extends Entity> EntityProvider getValue(Class<T> clazz) {
        return clazzToObject.get(clazz);
    }

    @Override
    public <T extends Entity> boolean containsKey(Class<T> key) {
        return clazzToObject.containsKey(key);
    }

    @Override
    public Set<Class<? extends Entity>> getEntityClasses() {
        return clazzToObject.keySet();
    }

    @Override
    public Collection<Map.Entry<Class<? extends Entity>, EntityProvider>> getEntity2EntryEntries() {
        return clazzToObject.entrySet();
    }

    @Nonnull
    @Override
    public EntityProvider register(@Nonnull EntityProvider obj) throws RegistrationException {
        if (containsKey(obj.getEntityType())) {
            throw new RegistrationException(String.format("Entity with class %s has already registered!", obj.getEntityType().getName()));
        }
        super.register(obj);
        clazzToObject.put(obj.getEntityType(), obj);
        return obj;
    }
}
