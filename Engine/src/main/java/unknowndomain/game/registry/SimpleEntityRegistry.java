package unknowndomain.game.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import unknowndomain.engine.entity.Entity;
import unknowndomain.engine.entity.EntityEntry;
import unknowndomain.engine.registry.RegistrationException;
import unknowndomain.engine.registry.game.EntityRegistry;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class SimpleEntityRegistry implements EntityRegistry {

    protected final BiMap<Class<? extends Entity>, EntityEntry> clazzToObject = HashBiMap.create();

    @Override
    public <T extends Entity> EntityEntry getValue(Class<T> clazz) {
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
    public Collection<Map.Entry<Class<? extends Entity>, EntityEntry>> getEntity2EntryEntries() {
        return clazzToObject.entrySet();
    }

    @Nonnull
    @Override
    public Class<EntityEntry> getEntryType() {
        return EntityEntry.class;
    }

    @Nonnull
    @Override
    public String getRegistryName() {
        return "entity";
    }

    @Nonnull
    @Override
    public EntityEntry register(@Nonnull EntityEntry obj) throws RegistrationException {
        if(containsKey(obj.getEntityType())){
            throw new RegistrationException(String.format("Entity with class %s has already registered!", obj.getEntityType().getName()));
        }
        clazzToObject.put(obj.getEntityType(),obj);
        return obj;
    }

    @Override
    public boolean containsValue(EntityEntry value) {
        return clazzToObject.containsValue(value);
    }

    @Override
    public EntityEntry getValue(String registryName) {
        return null;
    }

    @Override
    public String getKey(EntityEntry value) {
        return null;
    }

    @Override
    public boolean containsKey(String key) {
        return false;
    }

    @Override
    public Collection<EntityEntry> getValues() {
        return clazzToObject.values();
    }

    @Override
    public Set<String> getKeys() {
        return null;
    }

    @Override
    public int getId(EntityEntry obj) {
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
    public EntityEntry getValue(int id) {
        return null;
    }

    @Override
    public Collection<Map.Entry<String, EntityEntry>> getEntries() {
        return null;
    }
}
