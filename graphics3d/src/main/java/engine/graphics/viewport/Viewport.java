package engine.graphics.viewport;

import engine.graphics.Scene3D;
import engine.graphics.camera.Camera;
import engine.graphics.display.Window;
import engine.graphics.texture.FrameBuffer;
import org.joml.FrustumIntersection;
import org.joml.Matrix4fc;

public interface Viewport {

    int getWidth();

    int getHeight();

    void setSize(int width, int height);

    Scene3D getScene();

    void setScene(Scene3D scene);

    Camera getCamera();

    void setCamera(Camera camera);

    Matrix4fc getProjectionMatrix();

    Matrix4fc getViewMatrix();

    Matrix4fc getProjectionViewMatrix();

    FrustumIntersection getFrustum();

    FrameBuffer getFrameBuffer();

    Window getWindow();

    boolean isShowing();

    void show(FrameBuffer frameBuffer);

    void show(Window window);

    void show(Window window, FrameBuffer frameBuffer);

    void hide();
}
