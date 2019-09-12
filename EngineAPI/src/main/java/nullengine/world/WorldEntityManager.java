package nullengine.world;

import nullengine.entity.Entity;
import org.joml.Vector3dc;

import java.util.Collection;

public interface WorldEntityManager {

    World getWorld();

    <T extends Entity> T spawnEntity(Class<T> entityType, double x, double y, double z);

    <T extends Entity> T spawnEntity(Class<T> entityType, Vector3dc position);

    Entity spawnEntity(String providerName, double x, double y, double z);

    Entity spawnEntity(String providerName, Vector3dc position);

    Collection<Entity> getEntities();
}
