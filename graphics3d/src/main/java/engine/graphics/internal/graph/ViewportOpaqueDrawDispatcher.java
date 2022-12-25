package engine.graphics.internal.graph;

import engine.graphics.Geometry;
import engine.graphics.Scene3D;
import engine.graphics.graph.DrawDispatcher;
import engine.graphics.graph.Drawer;
import engine.graphics.graph.FrameContext;
import engine.graphics.graph.Renderer;
import engine.graphics.light.LightManager;
import engine.graphics.queue.RenderType;
import engine.graphics.shader.ShaderResource;
import engine.graphics.shader.Uniform;
import engine.graphics.shader.UniformBlock;
import engine.graphics.shader.UniformTexture;
import engine.graphics.viewport.Viewport;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class ViewportOpaqueDrawDispatcher implements DrawDispatcher {
    private final Matrix4f tempMatrix4f = new Matrix4f();

    private final Viewport viewport;

    private UniformBlock uniformLight;
    private Uniform uniformProjMatrix;
    private Uniform uniformViewMatrix;
    private Uniform uniformViewModelMatrix;
    private UniformTexture uniformTexture;

    public ViewportOpaqueDrawDispatcher(Viewport viewport) {
        this.viewport = viewport;
    }

    @Override
    public void init(Drawer drawer) {
        ShaderResource resource = drawer.getShaderResource();
        uniformLight = resource.getUniformBlock("Light", 2096);
        uniformProjMatrix = resource.getUniform("projMatrix");
        uniformViewMatrix = resource.getUniform("viewMatrix");
        uniformViewModelMatrix = resource.getUniform("viewModelMatrix");
        uniformTexture = resource.getUniformTexture("u_Texture");
    }

    @Override
    public void draw(FrameContext frameContext, Drawer drawer, Renderer renderer) {
        drawer.getShaderResource().setup();

        Scene3D scene = viewport.getScene();

        LightManager lightManager = scene.getLightManager();
        lightManager.setup(viewport.getCamera());
        uniformLight.set(0, lightManager);

        FrustumIntersection frustum = viewport.getFrustum();
        Matrix4fc viewMatrix = viewport.getViewMatrix();

        uniformProjMatrix.set(viewport.getProjectionMatrix());
        uniformViewMatrix.set(viewMatrix);

        for (Geometry geometry : scene.getRenderQueue().getGeometryList(RenderType.OPAQUE)) {
            if (geometry.shouldRender(frustum)) {
                uniformViewModelMatrix.set(viewMatrix.mul(geometry.getWorldTransform().getTransformMatrix(tempMatrix4f), tempMatrix4f));
                uniformTexture.setTexture(geometry.getTexture());
                renderer.drawMesh(geometry.getMesh());
            }
        }
    }
}
