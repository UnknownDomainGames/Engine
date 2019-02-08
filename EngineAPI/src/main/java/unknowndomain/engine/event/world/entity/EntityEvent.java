package unknowndomain.engine.event.world.entity;

import unknowndomain.engine.entity.Entity;
import unknowndomain.engine.event.Event;

public abstract class EntityEvent implements Event {
    private Entity entity;

    protected EntityEvent(Entity entity){
        this.entity = entity;
    }
}
