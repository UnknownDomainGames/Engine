package nullengine.client.rendering.scene;

import nullengine.client.rendering.camera.Camera;
import nullengine.client.rendering.display.Window;
import nullengine.util.Color;
import org.joml.FrustumIntersection;
import org.joml.Matrix4fc;

public interface ViewPort {

    int getWidth();

    int getHeight();

    void setSize(int width, int height);

    Scene getScene();

    void setScene(Scene scene);

    Color getClearColor();

    void setClearColor(Color color);

    boolean isClearColor();

    void setClearColor(boolean clearColor);

    boolean isClearDepth();

    void setClearDepth(boolean clearDepth);

    boolean isClearStencil();

    void setClearStencil(boolean clearStencil);

    void setClearMask(boolean color, boolean depth, boolean stencil);

    Camera getCamera();

    void setCamera(Camera camera);

    Matrix4fc getProjectionMatrix();

    Matrix4fc getViewMatrix();

    Matrix4fc getProjectionViewMatrix();

    FrustumIntersection getFrustum();

    void bindWindow(Window window);

    void unbindWindow();
}
