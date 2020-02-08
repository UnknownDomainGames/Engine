package engine.event.entity;

import engine.entity.Entity;

public final class EntityDestroyEvent extends EntityEvent {
    public EntityDestroyEvent(Entity entity) {
        super(entity);
    }
}
