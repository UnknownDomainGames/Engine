package unknowndomain.engine.entity;

import org.joml.AABBd;
import org.joml.Vector3d;
import org.joml.Vector3f;
import unknowndomain.engine.RuntimeObject;
import unknowndomain.engine.Tickable;
import unknowndomain.engine.item.Item;
import unknowndomain.engine.util.Owner;
import unknowndomain.engine.world.World;

@Owner(World.class)
public interface Entity extends RuntimeObject, Tickable {
    int getId();

    World getWorld();

    Vector3d getPosition();

    Vector3f getRotation();

    Vector3f getMotion();

    AABBd getBoundingBox();

    void destroy();
}
