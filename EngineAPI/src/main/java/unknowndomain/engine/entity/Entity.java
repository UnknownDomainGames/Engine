package unknowndomain.engine.entity;

import org.joml.AABBd;
import org.joml.Vector3f;
import unknowndomain.engine.RuntimeObject;
import unknowndomain.engine.Tickable;
import unknowndomain.engine.item.Item;

public interface Entity extends RuntimeObject, Tickable {
    int getId();

    Vector3f getPosition();

    Vector3f getRotation();

    Vector3f getMotion();

    AABBd getBoundingBox();

    void destroy();

    interface TwoHands {
        Item getMainHand();

        void setMainHand(Item mainHand);

        Item getOffHand();

        void setOffHand(Item offHand);
    }
}
