package nullengine.event.entity;

import nullengine.entity.Entity;

public final class EntityUnloadEvent extends EntityEvent {
    public EntityUnloadEvent(Entity entity) {
        super(entity);
    }
}
