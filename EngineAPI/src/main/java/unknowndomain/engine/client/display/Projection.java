package unknowndomain.engine.client.display;

import org.joml.Matrix4f;

public interface Projection {
    int getWidth();

    void setWidth(int width);

    int getHeight();

    void setHeight(int height);

    Matrix4f projection();
}
