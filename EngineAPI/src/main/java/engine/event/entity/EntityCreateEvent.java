package engine.event.entity;

import engine.entity.Entity;

public final class EntityCreateEvent extends EntityEvent {
    public EntityCreateEvent(Entity entity) {
        super(entity);
    }
}
