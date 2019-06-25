package nullengine.registry.game;

import nullengine.entity.Entity;
import nullengine.entity.EntityEntry;
import nullengine.registry.Registry;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface EntityRegistry extends Registry<EntityEntry> {

    <T extends Entity> EntityEntry getValue(Class<T> clazz);

    <T extends Entity> boolean containsKey(Class<T> key);

    Set<Class<? extends Entity>> getEntityClasses();

    Collection<Map.Entry<Class<? extends Entity>, EntityEntry>> getEntity2EntryEntries();
}
