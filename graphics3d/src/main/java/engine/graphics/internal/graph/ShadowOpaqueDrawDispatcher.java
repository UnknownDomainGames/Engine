package engine.graphics.internal.graph;

import engine.graphics.Geometry;
import engine.graphics.Scene3D;
import engine.graphics.graph.DrawDispatcher;
import engine.graphics.graph.Drawer;
import engine.graphics.graph.FrameContext;
import engine.graphics.graph.Renderer;
import engine.graphics.queue.RenderType;
import engine.graphics.shader.ShaderResource;
import engine.graphics.shader.UniformBlock;
import engine.graphics.viewport.Viewport;
import org.joml.Matrix4f;

public class ShadowOpaqueDrawDispatcher implements DrawDispatcher {
    private final Matrix4f tempMatrix4f = new Matrix4f();

    private final Viewport viewport;

    private UniformBlock uniformMatrices;

    public ShadowOpaqueDrawDispatcher(Viewport viewport) {
        this.viewport = viewport;
    }

    @Override
    public void init(Drawer drawer) {
        ShaderResource resource = drawer.getShaderResource();
        this.uniformMatrices = resource.getUniformBlock("Matrices", 128);
    }

    @Override
    public void draw(FrameContext frameContext, Drawer drawer, Renderer renderer) {
        drawer.getShaderResource().setup();
        Scene3D scene = viewport.getScene();
        uniformMatrices.set(0, viewport.getProjectionMatrix());
        for (Geometry geometry : scene.getRenderQueue().getGeometryList(RenderType.OPAQUE)) {
            Matrix4f transformMatrix = geometry.getWorldTransform().getTransformMatrix(tempMatrix4f);
            uniformMatrices.set(64, viewport.getViewMatrix().mul(transformMatrix, transformMatrix));
            renderer.drawMesh(geometry.getMesh());
        }
    }
}
