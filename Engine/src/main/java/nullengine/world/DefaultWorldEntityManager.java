package nullengine.world;

import nullengine.entity.Entity;
import nullengine.entity.EntityProvider;
import nullengine.event.entity.EntitySpawnEvent;
import nullengine.logic.Tickable;
import nullengine.registry.Registries;
import org.apache.commons.lang3.Validate;
import org.joml.Vector3dc;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultWorldEntityManager implements WorldEntityManager, Tickable {

    private final World world;

    private final AtomicInteger nextId = new AtomicInteger(0);

    private final Set<Entity> entities = new HashSet<>();
    private final Collection<Entity> unmodifiableEntities = Collections.unmodifiableSet(entities);

    public DefaultWorldEntityManager(World world) {
        this.world = world;
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public <T extends Entity> T spawnEntity(Class<T> entityType, Vector3dc position) {
        var provider = Registries.getEntityRegistry().getValue(entityType);
        return (T) spawnEntity(provider, position);
    }

    @Override
    public Entity spawnEntity(String providerName, Vector3dc position) {
        var provider = Registries.getEntityRegistry().getValue(providerName);
        return spawnEntity(provider, position);
    }

    private Entity spawnEntity(EntityProvider provider, Vector3dc position) {
        Validate.notNull(provider, "Entity provider is not found");
        return spawnEntity(provider.createEntity(nextId.getAndIncrement(), world, position));
    }

    private Entity spawnEntity(Entity entity) {
        if (entities.contains(entity)) {
            return entity;
        }

        var event = new EntitySpawnEvent.Pre(entity);
        if (world.getGame().getEventBus().post(event)) {
            return entity;
        }
        entities.add(entity);
        world.getGame().getEventBus().post(new EntitySpawnEvent.Post(entity));
        return entity;
    }


    @Override
    public Collection<Entity> getEntities() {
        return unmodifiableEntities;
    }

    @Override
    public void tick() {
        entities.forEach(Tickable::tick);
    }
}
