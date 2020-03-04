package engine.graphics.viewport;

import engine.graphics.Scene3D;
import engine.graphics.camera.Camera;
import engine.graphics.graph.RenderGraph;
import engine.graphics.graph.RenderGraphInfo;
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

    RenderGraph getRenderGraph();

    boolean isShowing();

    RenderGraph show();

    RenderGraph show(RenderGraphInfo renderGraph);

    void hide();
}
