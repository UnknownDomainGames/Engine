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
import engine.graphics.shader.UniformBlock;
import engine.graphics.shader.UniformTexture;
import engine.graphics.viewport.Viewport;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;

public class ViewportOpaqueDrawDispatcher implements DrawDispatcher {
    private final Matrix4f tempMatrix4f = new Matrix4f();

    private final Viewport viewport;

    private UniformBlock uniformMatrices;
    private UniformBlock uniformLight;
    private UniformTexture uniformTexture;

    public ViewportOpaqueDrawDispatcher(Viewport viewport) {
        this.viewport = viewport;
    }

    @Override
    public void init(Drawer drawer) {
        ShaderResource resource = drawer.getShaderResource();
        this.uniformMatrices = resource.getUniformBlock("Matrices", 192);
        this.uniformLight = resource.getUniformBlock("Light", 2096);
        this.uniformTexture = resource.getUniformTexture("u_Texture");
    }

    @Override
    public void draw(FrameContext frameContext, Drawer drawer, Renderer renderer) {
        drawer.getShaderResource().setup();

        Scene3D scene = viewport.getScene();

        LightManager lightManager = scene.getLightManager();
        lightManager.setup(viewport.getCamera());
        uniformLight.set(0, lightManager);

        uniformMatrices.set(0, viewport.getProjectionMatrix());
        uniformMatrices.set(64, viewport.getViewMatrix());

        FrustumIntersection frustum = viewport.getFrustum();
        for (Geometry geometry : scene.getRenderQueue().getGeometryList(RenderType.OPAQUE)) {
            if (geometry.shouldRender(frustum)) {
                uniformMatrices.set(128, geometry.getWorldTransform().getTransformMatrix(tempMatrix4f));
                uniformTexture.setTexture(geometry.getTexture());
                renderer.drawMesh(geometry.getMesh());
            }
        }
    }
}
