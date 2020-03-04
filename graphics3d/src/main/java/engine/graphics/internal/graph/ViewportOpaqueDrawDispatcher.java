package engine.graphics.internal.graph;

import engine.graphics.Scene3D;
import engine.graphics.graph.DrawDispatcher;
import engine.graphics.graph.Frame;
import engine.graphics.graph.Renderer;
import engine.graphics.queue.StandardRenderTypes;
import engine.graphics.shader.ShaderResource;
import engine.graphics.shader.UniformTexture;
import engine.graphics.viewport.Viewport;

public class ViewportOpaqueDrawDispatcher implements DrawDispatcher {
    private final Viewport viewport;

    private UniformTexture uniformTexture;

    public ViewportOpaqueDrawDispatcher(Viewport viewport) {
        this.viewport = viewport;
    }

    @Override
    public void init(ShaderResource resource) {
        this.uniformTexture = resource.getUniformTexture("u_Texture");
    }

    @Override
    public void draw(Frame frame, ShaderResource resource, Renderer renderer) {
        if (frame.isResized()) viewport.setSize(frame.getWidth(), frame.getHeight());
        resource.setUniform("u_ProjMatrix", viewport.getProjectionMatrix());
        resource.setUniform("u_ViewMatrix", viewport.getViewMatrix());
        Scene3D scene = viewport.getScene();
        scene.doUpdate(frame.getTickLastFrame());
        scene.getRenderQueue().getGeometryList(StandardRenderTypes.OPAQUE).forEach(geometry -> {
            resource.setUniform("u_ModelMatrix", geometry.getWorldTransform().toTransformMatrix());
            uniformTexture.set(geometry.getTexture());
            resource.refresh();
            renderer.drawMesh(geometry.getMesh());
        });
    }
}
