package nullengine.entity;

import nullengine.registry.RegistryEntry;
import nullengine.world.World;
import org.joml.Vector3dc;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class EntityProvider extends RegistryEntry.Impl<EntityProvider> {

    private final Class<? extends Entity> entityType;
    private final EntityFactory factory;
    private final EntitySerializer serializer;

    public EntityProvider(Class<? extends Entity> entityType, EntityFactory factory, EntitySerializer serializer) {
        this.entityType = entityType;
        this.factory = factory;
        this.serializer = serializer;
    }

    public Class<? extends Entity> getEntityType() {
        return entityType;
    }

    public EntityFactory getFactory() {
        return factory;
    }

    public EntitySerializer getSerializer() {
        return serializer;
    }

    public Entity createEntity(World world, Vector3dc position) {
        return factory.create(world, position);
    }

    public Entity load(World world, DataInputStream dis) {
        return serializer.load(world, dis, this);
    }

    public void save(World world, DataOutputStream dos, Entity entity) {
        serializer.save(world, dos, this, entity);
    }

    public static final class Builder {
        private Class<? extends Entity> entityType;
        private EntityFactory factory;
        private EntitySerializer serializer;
        private String registeredName;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder type(Class<? extends Entity> entityType) {
            this.entityType = entityType;
            return this;
        }

        public Builder factory(EntityFactory factory) {
            this.factory = factory;
            return this;
        }

        public Builder serializer(EntitySerializer serializer) {
            this.serializer = serializer;
            return this;
        }

        public Builder registeredName(String registeredName) {
            this.registeredName = registeredName;
            return this;
        }

        public EntityProvider build() {
            return new EntityProvider(entityType, factory, serializer).registerName(registeredName);
        }
    }
}
