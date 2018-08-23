package unknowndomain.engine.client.display;

import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * The lifecycle of camera is interesting... A camera can be killed after a game die.
 * <p>So im considering bind camera to {@link unknowndomain.engine.game.Game}</p>
 */
public interface Camera {
    Vector3f getPosition();

    Vector3f getLookAt();

    Vector3f getFrontVector();

    /**
     * Move the camera
     *
     * @param x x-translate
     * @param y y-translate
     * @param z z-translate
     */
    void move(float x, float y, float z);

    /**
     * Move the camera to the given position
     *
     * @param x x-translate
     * @param y y-translate
     * @param z z-translate
     */
    void moveTo(float x, float y, float z);

    void forward();

    void backward();

    void left();

    void right();

    void rotate(float dx, float dy);

    void rotateTo(float dx, float dy);

    /**
     * create view matrix for shader to use
     *
     * @return
     */
    Matrix4f view();

    /**
     * create projection matrix for shader to use
     *
     * @return
     */
    Matrix4f projection();

    interface Perspective extends Camera {

    }
}
