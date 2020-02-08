package engine.event.entity;

import engine.entity.Entity;

public final class EntityLoadEvent extends EntityEvent {
    public EntityLoadEvent(Entity entity) {
        super(entity);
    }
}
