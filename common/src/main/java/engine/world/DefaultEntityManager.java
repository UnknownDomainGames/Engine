package engine.world;

import engine.entity.Entity;
import engine.entity.EntityProvider;
import engine.event.EventBus;
import engine.event.entity.EntityCreateEvent;
import engine.event.entity.EntityDestroyEvent;
import engine.event.entity.EntitySpawnEvent;
import engine.event.entity.EntityTickEvent;
import engine.logic.Tickable;
import engine.registry.Registries;
import engine.world.hit.EntityHitResult;
import org.apache.commons.lang3.Validate;
import org.joml.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DefaultEntityManager implements EntityManager, Tickable {

    private final World world;
    private final EventBus eventBus;

    private final AtomicInteger nextId = new AtomicInteger(0);

    private final Set<Entity> entities = new HashSet<>();

    public DefaultEntityManager(World world) {
        this.world = world;
        this.eventBus = world.getGame().getEventBus();
    }

    @Override
    public <T extends Entity> T spawnEntity(Class<T> entityType, double x, double y, double z) {
        var provider = Registries.getEntityRegistry().getValue(entityType);
        return spawnEntity(provider, x, y, z);
    }

    @Override
    public <T extends Entity> T spawnEntity(Class<T> entityType, Vector3dc position) {
        return spawnEntity(entityType, position.x(), position.y(), position.z());
    }

    @Override
    public Entity spawnEntity(String providerName, double x, double y, double z) {
        var provider = Registries.getEntityRegistry().getValue(providerName);
        return spawnEntity(provider, x, y, z);
    }

    @Override
    public Entity spawnEntity(String providerName, Vector3dc position) {
        return spawnEntity(providerName, position.x(), position.y(), position.z());
    }

    @Override
    public void doDestroyEntity(Entity entity) {
        if (!world.equals(entity.getWorld())) {
            throw new IllegalStateException("Cannot destroy entity which is not belonged to this world");
        }

        if (!entity.isDestroyed()) {
            throw new IllegalStateException("Entity is not destroyed");
        }

        entities.remove(entity);
        eventBus.post(new EntityDestroyEvent(entity));
    }

    @SuppressWarnings("unchecked")
    private <T extends Entity> T spawnEntity(EntityProvider provider, double x, double y, double z) {
        Validate.notNull(provider, "Entity provider is not found");
        var entity = provider.createEntity(nextId.getAndIncrement(), world, x, y, z);
        eventBus.post(new EntityCreateEvent(entity));
        spawnEntity(entity);
        return (T) entity;
    }

    private void spawnEntity(Entity entity) {
        if (entities.contains(entity)) {
            return;
        }

        var event = new EntitySpawnEvent.Pre(entity);
        if (eventBus.post(event)) {
            return;
        }
        entities.add(entity);
        eventBus.post(new EntitySpawnEvent.Post(entity));
    }


    @Override
    public List<Entity> getEntities() {
        return new ArrayList<>(entities);
    }

    @Override
    public List<Entity> getEntities(Predicate<Entity> predicate) {
        return entities.stream().filter(predicate).collect(Collectors.toList());
    }

    @Override
    public <T extends Entity> List<T> getEntitiesWithType(Class<T> entityType) {
        return entities.stream()
                .filter(entity -> entityType.isAssignableFrom(entity.getClass()))
                .map(entityType::cast)
                .collect(Collectors.toList());
    }

    @Override
    public List<Entity> getEntitiesWithBoundingBox(AABBd boundingBox) {
        return getEntitiesWithBoundingBox(boundingBox.minX, boundingBox.minY, boundingBox.minZ, boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
    }

    private boolean testAABB(Entity entity, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        Vector3d position = entity.getPosition();
        if (entity.hasCollision()) {
            AABBd boundingBox = entity.getBoundingBox();
            double localMinX = minX - position.x(), localMinY = minY - position.y(), localMinZ = minZ - position.z(),
                    localMaxX = maxX - position.x(), localMaxY = maxY - position.y(), localMaxZ = maxZ - position.z();
            return Intersectiond.testAabAab(localMinX, localMinY, localMinZ, localMaxX, localMaxY, localMaxZ,
                    boundingBox.minX, boundingBox.minY, boundingBox.minZ, boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
        } else {
            return position.x() >= minX && position.y() >= minY && position.z() >= minZ
                    && position.x() <= maxX && position.y() <= maxY && position.z() <= maxZ;
        }
    }

    @Override
    public List<Entity> getEntitiesWithBoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return getEntities(entity -> testAABB(entity, minX, minY, minZ, maxX, maxY, maxZ));
    }

    @Override
    public List<Entity> getEntitiesWithSphere(double centerX, double centerY, double centerZ, double radius) {
        double radiusSquared = radius * radius;
        return getEntities(entity -> testSphere(entity, centerX, centerY, centerZ, radiusSquared));
    }

    private boolean testSphere(Entity entity, double centerX, double centerY, double centerZ, double radiusSquared) {
        Vector3d position = entity.getPosition();
        if (entity.hasCollision()) {
            double localX = centerX - position.x();
            double localY = centerY - position.y();
            double localZ = centerZ - position.z();
            return entity.getBoundingBox().testSphere(localX, localY, localZ, radiusSquared);
        } else {
            return position.distanceSquared(centerX, centerY, centerZ) <= radiusSquared;
        }
    }

    @Override
    public EntityHitResult raycastEntity(Vector3fc from, Vector3fc dir, float distance) {
        List<Entity> entities = getEntitiesWithSphere(from.x(), from.y(), from.z(), distance);
        entities.sort(Comparator.comparingDouble(entity -> entity.getPosition().distanceSquared(from.x(), from.y(), from.z())));
        Vector3f rayOffset = dir.normalize(new Vector3f()).mul(distance);
        Vector2d result = new Vector2d();
        for (Entity entity : entities) {
            if (!entity.hasCollision()) {
                continue;
            }

            Vector3d pos = entity.getPosition();
            Vector3f local = from.sub((float) pos.x(), (float) pos.y(), (float) pos.z(), new Vector3f());
            if (entity.getBoundingBox().intersectRay(local.x, local.y, local.z,
                    rayOffset.x, rayOffset.y, rayOffset.z, result)) {
                Vector3f hitPoint = local.add(rayOffset.mul((float) result.x, new Vector3f()));
                return new EntityHitResult(entity, hitPoint);
            }
        }
        return EntityHitResult.failure();
    }

    @Override
    public void tick() {
        for (Entity entity : entities) {
            entity.tick();
            eventBus.post(new EntityTickEvent(entity));
        }
    }
}
