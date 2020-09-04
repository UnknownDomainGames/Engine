package engine.registry.game;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import engine.entity.Entity;
import engine.entity.EntityProvider;
import engine.registry.RegistrationException;
import engine.registry.impl.SynchronizableIdRegistry;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public final class EntityRegistryImpl extends SynchronizableIdRegistry<EntityProvider> implements EntityRegistry {

    protected final BiMap<Class<? extends Entity>, EntityProvider> clazzToObject = HashBiMap.create();

    public EntityRegistryImpl() {
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
