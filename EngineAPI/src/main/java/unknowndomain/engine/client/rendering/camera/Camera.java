package unknowndomain.engine.client.rendering.camera;

import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * The lifecycle of camera is interesting... A camera can be killed after a game die.
 * <p>So im considering bind camera to {@link unknowndomain.engine.game.Game}</p>
 */
public interface Camera {

    Vector3f UP_VECTOR = new Vector3f(0, 1, 0);

    Vector3f getPosition(float parTick);

    Vector3f getLookAt(float parTick);

    default Vector3f getFrontVector(float parTick) {
        return getLookAt(parTick).sub(getPosition(0), new Vector3f()).normalize();
    }

    /**
     * parse view matrix for shader to use
     *
     * @return
     */
    default Matrix4f view(float parTick) {
        return new Matrix4f().lookAt(getPosition(parTick), getLookAt(parTick), UP_VECTOR);
    }
}
