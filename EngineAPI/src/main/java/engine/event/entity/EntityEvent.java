package engine.event.entity;

import engine.entity.Entity;
import engine.event.Event;

public abstract class EntityEvent implements Event {
    private Entity entity;

    public EntityEvent(Entity entity) {
        this.entity = entity;
    }
}
