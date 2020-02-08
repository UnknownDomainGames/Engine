package engine.event.entity;

import engine.entity.Entity;

public final class EntityUnloadEvent extends EntityEvent {
    public EntityUnloadEvent(Entity entity) {
        super(entity);
    }
}
