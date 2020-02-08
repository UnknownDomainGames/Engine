package engine.world.hit;

import engine.entity.Entity;
import org.joml.Vector3fc;

import java.util.function.Consumer;

public class EntityHitResult extends HitResult {

    private static final EntityHitResult FAILURE = new EntityHitResult();

    private final Entity entity;
    private final Vector3fc hitPoint;

    public static EntityHitResult failure() {
        return FAILURE;
    }

    public EntityHitResult(Entity entity, Vector3fc hitPoint) {
        this(Type.ENTITY, entity, hitPoint);
    }

    private EntityHitResult() {
        this(Type.MISS, null, null);
    }

    private EntityHitResult(Type type, Entity entity, Vector3fc hitPoint) {
        super(type);
        this.entity = entity;
        this.hitPoint = hitPoint;
    }

    public Entity getEntity() {
        return entity;
    }

    public Vector3fc getHitPoint() {
        return hitPoint;
    }

    public void ifSuccess(Consumer<EntityHitResult> consumer) {
        if (isSuccess()) {
            consumer.accept(this);
        }
    }
}
