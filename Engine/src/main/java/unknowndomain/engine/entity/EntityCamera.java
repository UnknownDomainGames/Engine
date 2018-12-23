package unknowndomain.engine.entity;

import org.joml.AABBd;

public class EntityCamera extends EntityBase {

    public EntityCamera(int id) {
        super(id);
        setBoundingBox(new AABBd(0, 0, 0, 0, 0, 0));
    }
}
