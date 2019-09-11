package nullengine.event.entity;

import nullengine.entity.Entity;

public class EntityDestroyEvent extends EntityEvent {
    public EntityDestroyEvent(Entity entity) {
        super(entity);
    }
}
