package engine.graphics.internal.graph;

import engine.graphics.Scene3D;
import engine.graphics.graph.DrawDispatcher;
import engine.graphics.graph.Frame;
import engine.graphics.graph.Renderer;
import engine.graphics.queue.StandardRenderTypes;
import engine.graphics.shader.ShaderResource;
import engine.graphics.shader.UniformBlock;
import engine.graphics.shader.UniformTexture;
import engine.graphics.viewport.Viewport;
import org.joml.Matrix4fc;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;

public class ViewportOpaqueDrawDispatcher implements DrawDispatcher {
    private final Viewport viewport;

    private static class Matrices implements UniformBlock.Value {
        private Matrix4fc projMatrix;
        private Matrix4fc viewMatrix;
        private Matrix4fc modelMatrix;

        public Matrices(Matrix4fc projMatrix, Matrix4fc viewMatrix, Matrix4fc modelMatrix) {
            this.projMatrix = projMatrix;
            this.viewMatrix = viewMatrix;
            this.modelMatrix = modelMatrix;
        }

        @Override
        public ByteBuffer write(MemoryStack stack) {
            ByteBuffer buffer = stack.malloc(192).limit(192);
            projMatrix.get(0, buffer);
            viewMatrix.get(64, buffer);
            modelMatrix.get(128, buffer);
            return buffer;
        }
    }

    private UniformBlock uniformMatrices;
    private UniformTexture uniformTexture;

    public ViewportOpaqueDrawDispatcher(Viewport viewport) {
        this.viewport = viewport;
    }

    @Override
    public void init(ShaderResource resource) {
        this.uniformMatrices = resource.getUniformBlock("Transformation");
        this.uniformTexture = resource.getUniformTexture("u_Texture");
    }

    @Override
    public void draw(Frame frame, ShaderResource resource, Renderer renderer) {
        if (frame.isResized()) viewport.setSize(frame.getWidth(), frame.getHeight());
        Scene3D scene = viewport.getScene();
        scene.doUpdate(frame.getTickLastFrame());
        scene.getRenderQueue().getGeometryList(StandardRenderTypes.OPAQUE).forEach(geometry -> {
            uniformMatrices.set(new Matrices(
                    viewport.getProjectionMatrix(),
                    viewport.getViewMatrix(),
                    geometry.getWorldTransform().toTransformMatrix()));
            uniformTexture.set(geometry.getTexture());
            resource.refresh();
            renderer.drawMesh(geometry.getMesh());
        });
    }
}
