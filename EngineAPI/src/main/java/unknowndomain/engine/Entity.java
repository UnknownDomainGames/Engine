package unknowndomain.engine;

import org.joml.Vector3f;

import java.util.UUID;

public interface Entity extends RuntimeObject, Tickable {
    UUID getUUID();

    Vector3f getPosition();

    Vector3f getMotion();
}
