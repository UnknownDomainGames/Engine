package engine.graphics.internal.graph;

import engine.graphics.Geometry;
import engine.graphics.Scene3D;
import engine.graphics.graph.DrawDispatcher;
import engine.graphics.graph.Drawer;
import engine.graphics.graph.Frame;
import engine.graphics.graph.Renderer;
import engine.graphics.queue.RenderType;
import engine.graphics.shader.ShaderResource;
import engine.graphics.shader.Uniform;
import engine.graphics.viewport.Viewport;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class ShadowOpaqueDrawDispatcher implements DrawDispatcher {
    private final Matrix4f tempMatrix4f = new Matrix4f();

    private final Viewport viewport;

    private Uniform uniformProjMatrix;
    private Uniform uniformViewModelMatrix;

    public ShadowOpaqueDrawDispatcher(Viewport viewport) {
        this.viewport = viewport;
    }

    @Override
    public void init(Drawer drawer) {
        ShaderResource resource = drawer.getShaderResource();
        uniformProjMatrix = resource.getUniform("projMatrix");
        uniformViewModelMatrix = resource.getUniform("viewModelMatrix");
    }

    @Override
    public void draw(Frame frame, Drawer drawer, Renderer renderer) {
        drawer.getShaderResource().setup();
        Scene3D scene = viewport.getScene();
        Matrix4fc viewMatrix = viewport.getViewMatrix();
        uniformProjMatrix.set(viewport.getProjectionMatrix());
        for (Geometry geometry : scene.getRenderQueue().getGeometryList(RenderType.OPAQUE)) {
            uniformViewModelMatrix.set(viewMatrix.mul(geometry.getWorldTransform().getTransformMatrix(tempMatrix4f), tempMatrix4f));
            renderer.drawMesh(geometry.getMesh());
        }
    }
}
