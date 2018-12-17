package unknowndomain.engine.entity;

import com.google.common.collect.ImmutableMap;
import org.joml.AABBd;
import org.joml.Vector3d;
import org.joml.Vector3f;
import unknowndomain.engine.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class EntityBase implements Entity {
    private int id;

    private World world;
    private Vector3d position = new Vector3d();
    private Vector3f rotation = new Vector3f();
    private Vector3f motion = new Vector3f();
    private AABBd boundingBox;
    private ImmutableMap<String, Object> behaviors;

    public EntityBase(int id) {
        this(id, ImmutableMap.of());
    }

    public EntityBase(int id, ImmutableMap<String, Object> behaviors) {
        this.id = id;
        this.behaviors = behaviors;
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public Vector3d getPosition() {
        return position;
    }

    @Override
    public AABBd getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(AABBd boundingBox) {
        this.boundingBox = boundingBox;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void tick() {
    }

    @Override
    public Vector3f getRotation() {
        return rotation;
    }

    @Override
    public void destroy() {
    }

    @Override
    public Vector3f getMotion() {
        return motion;
    }

    @Nullable
    @Override
    public <T> T getComponent(@Nonnull String name) {
        return null;
    }

    @Nullable
    @Override
    public <T> T getComponent(@Nonnull Class<T> type) {
        return null;
    }

    @Nullable
    @Override
    public <T> T getBehavior(Class<T> type) {
        return (T) behaviors.get(type.getName());
    }
}
