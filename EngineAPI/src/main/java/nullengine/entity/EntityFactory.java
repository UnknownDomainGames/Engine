package nullengine.entity;

import nullengine.world.World;
import org.joml.Vector3dc;

@FunctionalInterface
public interface EntityFactory<T extends Entity> {

    T create(World world, Vector3dc position);
}
