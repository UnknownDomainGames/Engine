package engine.registry.game;

import engine.entity.Entity;
import engine.entity.EntityProvider;
import engine.registry.Registry;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface EntityRegistry extends Registry<EntityProvider> {

    <T extends Entity> EntityProvider getValue(Class<T> clazz);

    <T extends Entity> boolean containsKey(Class<T> key);

    Set<Class<? extends Entity>> getEntityClasses();

    Collection<Map.Entry<Class<? extends Entity>, EntityProvider>> getEntity2EntryEntries();
}
