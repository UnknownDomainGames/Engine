package nullengine.entity;

import nullengine.component.Component;
import nullengine.component.ComponentContainer;
import nullengine.event.world.entity.EntityEvent;
import nullengine.world.World;
import org.joml.AABBd;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;

public abstract class BaseEntity implements Entity {
    private int id;

    private World world;
    private Vector3d position = new Vector3d();
    private Vector3f rotation = new Vector3f();
    private Vector3f motion = new Vector3f();
    private AABBd boundingBox;

    private final ComponentContainer components;

    public BaseEntity(int id, World world) {
        this.id = id;
        this.world = world;
        this.components = new ComponentContainer();
    }

    public BaseEntity(int id, World world, Vector3dc position) {
        this(id, world);
        this.position.set(position);
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

    public void onDeath(Object source){

    }

    @Override
    public Vector3f getMotion() {
        return motion;
    }

    @Nonnull
    @Override
    public <T extends Component> Optional<T> getComponent(@Nonnull Class<T> type) {
        return components.getComponent(type);
    }

    @Override
    public <T extends Component> boolean hasComponent(@Nonnull Class<T> type) {
        return components.hasComponent(type);
    }

    @Override
    public <T extends Component> void setComponent(@Nonnull Class<T> type, @Nullable T value) {
        components.setComponent(type, value);
    }

    @Override
    public <T extends Component> void removeComponent(@Nonnull Class<T> type) {
        components.removeComponent(type);
    }

    @Nonnull
    @Override
    public Set<Class<?>> getComponents() {
        return components.getComponents();
    }
}
