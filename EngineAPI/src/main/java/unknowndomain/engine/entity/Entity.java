package unknowndomain.engine.entity;

import org.joml.AABBd;
import org.joml.Vector3d;
import org.joml.Vector3f;
import unknowndomain.engine.Tickable;
import unknowndomain.engine.component.RuntimeObject;
import unknowndomain.engine.world.World;

public interface Entity extends RuntimeObject, Tickable {
    int getId();

    World getWorld();

    Vector3d getPosition();

    Vector3f getRotation();

    @Deprecated
    Vector3f getMotion();

    AABBd getBoundingBox();

    void destroy();
}
