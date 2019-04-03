package unknowndomain.engine.entity;

import org.joml.AABBd;
import org.joml.Vector3d;
import org.joml.Vector3f;
import unknowndomain.engine.component.Component;
import unknowndomain.engine.world.World;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class EntityBase implements Entity {
    private int id;

    private World world;
    private Vector3d position = new Vector3d();
    private Vector3f rotation = new Vector3f();
    private Vector3f motion = new Vector3f();
    private AABBd boundingBox;

    private final Map<Class<?>, Object> components;

    public EntityBase(int id, World world) {
        this.id = id;
        this.world = world;
        this.components = new HashMap<>();
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

    @Nonnull
    @Override
    public <T extends Component> Optional<T> getComponent(@Nonnull Class<T> type) {
        return Optional.empty();
    }

    @Override
    public <T extends Component> boolean hasComponent(@Nonnull Class<T> type) {
        return false;
    }

    @Override
    public <T extends Component> void setComponent(@Nonnull Class<T> type, @Nonnull T value) {

    }

    @Override
    public <T extends Component> void removeComponent(@Nonnull Class<T> type) {

    }
}
