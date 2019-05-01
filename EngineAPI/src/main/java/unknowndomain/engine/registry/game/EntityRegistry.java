package unknowndomain.engine.registry.game;

import unknowndomain.engine.entity.Entity;
import unknowndomain.engine.entity.EntityEntry;
import unknowndomain.engine.registry.Registry;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface EntityRegistry extends Registry<EntityEntry> {

    <T extends Entity> EntityEntry getValue(Class<T> clazz);

    <T extends Entity> boolean containsKey(Class<T> key);

    Set<Class<? extends Entity>> getEntityClasses();

    Collection<Map.Entry<Class<? extends Entity>, EntityEntry>> getEntity2EntryEntries();
}
