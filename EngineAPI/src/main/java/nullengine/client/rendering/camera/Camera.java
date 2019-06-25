package nullengine.client.rendering.camera;

import nullengine.game.Game;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * The lifecycle of camera is interesting... A camera can be killed after a game die.
 * <p>So im considering bind camera to {@link Game}</p>
 */
public interface Camera {

    Vector3fc UP_VECTOR = new Vector3f(0, 1, 0);

    void update(float partial);

    Vector3fc getPosition();

    Vector3fc getLookAt();

    Vector3fc getFrontVector();

    /**
     * parse view matrix for shader to use
     *
     * @return
     */
    Matrix4fc getViewMatrix();
}
