package nullengine.event.entity;

import nullengine.entity.Entity;

public final class EntityLoadEvent extends EntityEvent {
    public EntityLoadEvent(Entity entity) {
        super(entity);
    }
}
