package unknowndomain.engine.entity;

import org.joml.Vector3dc;
import unknowndomain.engine.world.World;

@FunctionalInterface
public interface EntityFactory<T extends Entity> {

    T create(World world, Vector3dc position);
}
