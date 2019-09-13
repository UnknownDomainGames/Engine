package nullengine.event.entity;

import nullengine.entity.Entity;

public final class EntityTickEvent extends EntityEvent {
    public EntityTickEvent(Entity entity) {
        super(entity);
    }
}
