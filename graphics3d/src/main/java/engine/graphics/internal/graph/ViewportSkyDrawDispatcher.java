package engine.graphics.internal.graph;

import engine.graphics.Geometry;
import engine.graphics.graph.DrawDispatcher;
import engine.graphics.graph.Drawer;
import engine.graphics.graph.FrameContext;
import engine.graphics.graph.Renderer;
import engine.graphics.queue.RenderType;
import engine.graphics.shader.ShaderResource;
import engine.graphics.shader.Uniform;
import engine.graphics.shader.UniformTexture;
import engine.graphics.viewport.Viewport;

public class ViewportSkyDrawDispatcher implements DrawDispatcher {
    private final Viewport viewport;

    private Uniform uniformProjMatrix;
    private Uniform uniformViewMatrix;
    private UniformTexture uniformTexture;

    public ViewportSkyDrawDispatcher(Viewport viewport) {
        this.viewport = viewport;
    }

    @Override
    public void init(Drawer drawer) {
        ShaderResource resource = drawer.getShaderResource();
        uniformProjMatrix = resource.getUniform("projMatrix");
        uniformViewMatrix = resource.getUniform("viewMatrix");
        uniformTexture = resource.getUniformTexture("u_Texture");
    }

    @Override
    public void draw(FrameContext frameContext, Drawer drawer, Renderer renderer) {
        drawer.getShaderResource().setup();
        uniformProjMatrix.set(viewport.getProjectionMatrix());
        uniformViewMatrix.set(viewport.getViewMatrix());
        for (Geometry geometry : viewport.getScene().getRenderQueue().getGeometryList(RenderType.SKY)) {
            uniformTexture.setTexture(geometry.getTexture());
            renderer.drawMesh(geometry.getMesh());
        }
    }
}
