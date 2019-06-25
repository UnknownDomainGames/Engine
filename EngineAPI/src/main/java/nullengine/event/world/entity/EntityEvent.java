package nullengine.event.world.entity;

import nullengine.entity.Entity;
import nullengine.event.Event;

public abstract class EntityEvent implements Event {
    private Entity entity;

    protected EntityEvent(Entity entity) {
        this.entity = entity;
    }
}
