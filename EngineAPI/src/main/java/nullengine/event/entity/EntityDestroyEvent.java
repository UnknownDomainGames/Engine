package nullengine.event.entity;

import nullengine.entity.Entity;

public final class EntityDestroyEvent extends EntityEvent {
    public EntityDestroyEvent(Entity entity) {
        super(entity);
    }
}
