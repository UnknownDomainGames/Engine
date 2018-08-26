package unknowndomain.engine.client.display;

import org.joml.Matrix4f;

/**
 * The object provides projection matrix. For the people who doesn't have gl context.
 * Read
 * <p>
 * http://www.opengl-tutorial.org/beginners-tutorials/tutorial-3-matrices/
 * </p>
 */
public interface Projection {
    int getWidth();

    void setWidth(int width);

    int getHeight();

    void setHeight(int height);

    Matrix4f projection();
}
