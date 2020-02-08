package engine.event.entity;

import engine.entity.Entity;

public final class EntityTickEvent extends EntityEvent {
    public EntityTickEvent(Entity entity) {
        super(entity);
    }
}
