package unknowndomain.engine;

import org.joml.Vector3f;
import unknowndomain.engine.RuntimeObject;

import java.util.UUID;

public interface Entity extends RuntimeObject, Tickable {
    UUID getUUid();

    Vector3f getPosition();

    Vector3f getMotion();
}
