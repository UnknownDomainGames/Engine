package engine.graphics.internal.graph;

import engine.graphics.LightManager;
import engine.graphics.Scene3D;
import engine.graphics.graph.DrawDispatcher;
import engine.graphics.graph.Drawer;
import engine.graphics.graph.FrameContext;
import engine.graphics.graph.Renderer;
import engine.graphics.light.DirectionalLight;
import engine.graphics.queue.RenderType;
import engine.graphics.shader.ShaderResource;
import engine.graphics.shader.UniformBlock;
import engine.graphics.shader.UniformTexture;
import engine.graphics.viewport.Viewport;
import org.joml.Matrix4fc;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;

public class ViewportOpaqueDrawDispatcher implements DrawDispatcher {
    private final Viewport viewport;

    private UniformBlock uniformMatrices;
    private UniformBlock uniformLight;
    private UniformTexture uniformTexture;

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
        public ByteBuffer get(MemoryStack stack) {
            return get(stack.malloc(192));
        }

        @Override
        public ByteBuffer get(int index, ByteBuffer buffer) {
            projMatrix.get(index, buffer);
            viewMatrix.get(index + 64, buffer);
            modelMatrix.get(index + 128, buffer);
            return buffer;
        }
    }

    public ViewportOpaqueDrawDispatcher(Viewport viewport) {
        this.viewport = viewport;
    }

    @Override
    public void init(Drawer drawer) {
        ShaderResource resource = drawer.getShaderResource();
        this.uniformMatrices = resource.getUniformBlock("Matrices");
        this.uniformLight = resource.getUniformBlock("DirLight");
        this.uniformTexture = resource.getUniformTexture("u_Texture");
    }

    @Override
    public void draw(FrameContext frameContext, Drawer drawer, Renderer renderer) {
        Scene3D scene = viewport.getScene();
        ShaderResource resource = drawer.getShaderResource();
        LightManager lightManager = scene.getLightManager();
        lightManager.setup(viewport.getViewMatrix());
        DirectionalLight directionalLight = lightManager.getDirectionalLights().get(0);
        uniformLight.set(directionalLight);
        scene.getRenderQueue().getGeometryList(RenderType.OPAQUE).forEach(geometry -> {
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
