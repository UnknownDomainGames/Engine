package engine.event.entity;

import engine.event.action.InteractEvent;
import engine.event.entity.cause.EntityInteractCause;
import engine.world.hit.EntityHitResult;
import org.joml.Vector3fc;

import java.util.Optional;

public abstract class EntityInteractEvent extends EntityEvent implements InteractEvent {

    private final EntityHitResult hitResult;
    private final EntityInteractCause cause;

    private EntityInteractEvent(EntityHitResult hitResult, EntityInteractCause cause) {
        super(hitResult.getEntity());
        this.hitResult = hitResult;
        this.cause = cause;
    }

    public EntityInteractCause getCause() {
        return cause;
    }

    @Override
    public Optional<Vector3fc> getInteractionPoint() {
        return Optional.of(hitResult.getHitPoint());
    }

    public static final class Click extends EntityInteractEvent {

        public Click(EntityHitResult hitResult, EntityInteractCause cause) {
            super(hitResult, cause);
        }
    }

    public static final class Activate extends EntityInteractEvent {

        public Activate(EntityHitResult hitResult, EntityInteractCause cause) {
            super(hitResult, cause);
        }
    }
}
