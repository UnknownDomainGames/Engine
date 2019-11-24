package nullengine.client.rendering.scene;

import nullengine.client.rendering.camera.Camera;
import org.joml.FrustumIntersection;
import org.joml.Matrix4fc;

public interface ViewPort {

    int getWidth();

    int getHeight();

    void setSize(int width, int height);

    Camera getCamera();

    void setCamera(Camera camera);

    Matrix4fc getProjectionMatrix();

    Matrix4fc getViewMatrix();

    Matrix4fc getProjectionViewMatrix();

    FrustumIntersection getFrustum();
}
