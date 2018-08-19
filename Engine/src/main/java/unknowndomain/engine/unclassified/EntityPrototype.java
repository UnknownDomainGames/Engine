package unknowndomain.engine.unclassified;

import unknowndomain.engine.Prototype;
import unknowndomain.engine.entity.Entity;
import unknowndomain.engine.registry.RegistryEntry;
import unknowndomain.engine.world.World;

public interface EntityPrototype extends Prototype<Entity, World>, RegistryEntry<EntityPrototype> {
}
