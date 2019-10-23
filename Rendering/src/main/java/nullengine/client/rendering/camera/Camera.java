package nullengine.client.rendering.camera;

import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public interface Camera {

    Vector3fc UP_VECTOR = new Vector3f(0, 1, 0);

    void update(float partial);

    Vector3fc getPosition();

    Vector3fc getLookAt();

    Vector3fc getFrontVector();

    Matrix4fc getViewMatrix();
}
