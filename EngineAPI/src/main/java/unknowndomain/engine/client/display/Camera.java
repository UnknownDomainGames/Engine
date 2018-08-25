package unknowndomain.engine.client.display;

import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * The lifecycle of camera is interesting... A camera can be killed after a game die.
 * <p>So im considering bind camera to {@link unknowndomain.engine.game.Game}</p>
 */
public interface Camera {
    Vector3f UP_VECTOR = new Vector3f(0, 1, 0);

    Vector3f getPosition();

    Vector3f getLookAt();

    default Vector3f getFrontVector() {
        return getLookAt().sub(getPosition(), new Vector3f());
    }

    /**
     * create view matrix for shader to use
     *
     * @return
     */
    default Matrix4f view() {
        return new Matrix4f().lookAt(getPosition(), getLookAt(), UP_VECTOR);
    }
}
