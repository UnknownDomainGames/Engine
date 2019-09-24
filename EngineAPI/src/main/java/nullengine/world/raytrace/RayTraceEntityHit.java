package nullengine.world.raytrace;

import nullengine.entity.Entity;
import org.joml.Vector3fc;

import java.util.function.Consumer;

public class RayTraceEntityHit {

    private static final RayTraceEntityHit FAILURE = new RayTraceEntityHit(null, null);

    private final Entity entity;
    private final Vector3fc hitPoint;

    public static RayTraceEntityHit failure() {
        return FAILURE;
    }

    public RayTraceEntityHit(Entity entity, Vector3fc hitPoint) {
        this.entity = entity;
        this.hitPoint = hitPoint;
    }

    public Entity getEntity() {
        return entity;
    }

    public Vector3fc getHitPoint() {
        return hitPoint;
    }

    public boolean isSuccess() {
        return entity != null;
    }

    public void ifSuccess(Consumer<RayTraceEntityHit> consumer) {
        if (isSuccess()) {
            consumer.accept(this);
        }
    }
}
