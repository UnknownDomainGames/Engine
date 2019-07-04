package nullengine.entity;

import nullengine.component.GameObject;
import nullengine.logic.Tickable;
import nullengine.world.World;
import org.joml.AABBd;
import org.joml.Vector3d;
import org.joml.Vector3f;

public interface Entity extends GameObject, Tickable {
    int getId();

    World getWorld();

    Vector3f getPosition();

    Vector3f getRotation();

    @Deprecated
    Vector3f getMotion();

    AABBd getBoundingBox();

    void destroy();
}
