package unknowndomain.engine.entity;

import unknowndomain.engine.Prototype;
import unknowndomain.engine.entity.Entity;
import unknowndomain.engine.registry.RegistryEntry;
import unknowndomain.engine.world.World;

public interface EntityType extends Prototype<Entity, World>, RegistryEntry<EntityType> {
}
