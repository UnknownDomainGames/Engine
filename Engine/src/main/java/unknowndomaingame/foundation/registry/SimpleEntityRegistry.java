package unknowndomaingame.foundation.registry;

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

public class SimpleEntityRegistry implements EntityRegistry {

    protected final BiMap<Class<? extends Entity>, EntityProvider> clazzToObject = HashBiMap.create();

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
    public Class<EntityProvider> getEntryType() {
        return EntityProvider.class;
    }

    @Nonnull
    @Override
    public String getRegistryName() {
        return "entity";
    }

    @Nonnull
    @Override
    public EntityProvider register(@Nonnull EntityProvider obj) throws RegistrationException {
        if (containsKey(obj.getEntityType())) {
            throw new RegistrationException(String.format("Entity with class %s has already registered!", obj.getEntityType().getName()));
        }
        clazzToObject.put(obj.getEntityType(), obj);
        return obj;
    }

    @Override
    public boolean containsValue(EntityProvider value) {
        return clazzToObject.containsValue(value);
    }

    @Override
    public EntityProvider getValue(String registryName) {
        return null;
    }

    @Override
    public String getKey(EntityProvider value) {
        return null;
    }

    @Override
    public boolean containsKey(String key) {
        return false;
    }

    @Override
    public Collection<EntityProvider> getValues() {
        return clazzToObject.values();
    }

    @Override
    public Set<String> getKeys() {
        return null;
    }

    @Override
    public int getId(EntityProvider obj) {
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
    public EntityProvider getValue(int id) {
        return null;
    }

    @Override
    public Collection<Map.Entry<String, EntityProvider>> getEntries() {
        return null;
    }
}
