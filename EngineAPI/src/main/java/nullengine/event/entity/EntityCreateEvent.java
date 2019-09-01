package nullengine.event.entity;

import nullengine.entity.Entity;

public final class EntityCreateEvent extends EntityEvent {
    public EntityCreateEvent(Entity entity) {
        super(entity);
    }
}
